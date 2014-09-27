package downloader;

// Constants
final WORKING_DIR = "f:\\PODCASTS\\";
final String DEFINITIONS_FILE = "definitions.txt";

// Replacements
final String REPLACE_SOURCE = "www2.rozhlas.cz/stream";
final String REPLACE_TARGET = "media.rozhlas.cz/_audio";


// Implementation begin
def definitions = new File(WORKING_DIR + DEFINITIONS_FILE);

if (definitions.exists()) {
	println "Definitions found: ${definitions.getAbsolutePath()}."
	
	idx = 0;
	definitions.eachLine { line ->
		
		// Skip empty lines
		if (line == null || line.empty) {
			return;
		}
		
		print "Processing ${line}"
		
		String target = line.substring(line.lastIndexOf('/') + 1);
		def targetFile = new File(WORKING_DIR + target);
		
		if (targetFile.exists()) {
			targetFile.delete();
		}
		
		line = line.replaceFirst(REPLACE_SOURCE, REPLACE_TARGET);
		targetFile << line.toURL().openStream();
		
		println " ... downloaded to ${targetFile}"
		idx++;
	}
	
	println "Processed ${idx} files. Done."
	
	// Make the definitions file empty
	definitions.write("");
	
} else {
	println "Definitions not found: ${definitions.getAbsolutePath()}. Quitting."
}
