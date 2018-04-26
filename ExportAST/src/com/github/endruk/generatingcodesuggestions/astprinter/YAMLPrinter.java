package com.github.endruk.generatingcodesuggestions.astprinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import com.github.endruk.generatingcodesuggestions.utils.FileWriter;
import com.github.javaparser.ast.Node;

public class YAMLPrinter extends ASTPrinter {
	private com.github.javaparser.printer.YamlPrinter printer;
	private boolean escapeWhitespaces = true;
	public YAMLPrinter(boolean escapeWhitespaces) {
		printer = new com.github.javaparser.printer.YamlPrinter(true);
		this.escapeWhitespaces = escapeWhitespaces;
	}
	
	@Override
	public void print(Node node, String targetFilepath) {
		String serializedTree = printer.output(node);
		if(escapeWhitespaces) {
			serializedTree = processYaml(serializedTree);
		}
		FileWriter writer = new FileWriter();
		writer.writeToFile(targetFilepath, serializedTree);
	}
	
	private String processYaml(String input) {
		String result = "";
		BufferedReader bufReader = new BufferedReader(new StringReader(input));
		String line = null;
		try {
			while((line=bufReader.readLine()) != null) {
				// ignore start --- and end ... of yaml file
				if(line.startsWith("---") || line.startsWith("...")) {
					continue;
				}
				// count the whitespaces at beginning of line
				int whitespaces = line.indexOf(line.trim());
				line = "<W" + String.valueOf(whitespaces) + "> " + line.trim();
				result += line + "\n";
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
