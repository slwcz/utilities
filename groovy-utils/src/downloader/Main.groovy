package downloader;

// Constants
final WORKING_DIR = "f:\\PODCASTS\\";
final String DEFINITIONS_FILE = "definitions.txt";

// Replacements
// Supported format examples: prehravac.rozhlas.cz/audio/3304377
def KNOWN_PATTERNS = [ ~/prehravac.rozhlas.cz\/audio\/([0-9]+)$/ ]
def TARGET_PATTERN = "http://media.rozhlas.cz/_audio/<identifier>.mp3"


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
		
		for (def pattern : KNOWN_PATTERNS) {
			def matcher = line =~  pattern
			
			// If current line matches a known pattern
			if (matcher.size() > 0) {
				def identifier = matcher[0][1]
				line = TARGET_PATTERN.replace("<identifier>", identifier);
				break
			}
		}
		
		String target = line.substring(line.lastIndexOf('/') + 1);
		def targetFile = new File(WORKING_DIR + target);
		
		if (targetFile.exists()) {
			targetFile.delete();
		}
		
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
