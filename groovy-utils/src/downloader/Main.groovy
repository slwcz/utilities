package downloader;

// Constants
final WORKING_DIR = "f:\\PODCASTS\\";
final String DEFINITIONS_FILE = "definitions.txt";

// Replacements
// Supported format examples: prehravac.rozhlas.cz/audio/3304377
def KNOWN_PATTERNS = [ ~/prehravac.rozhlas.cz\/audio\/([0-9]+)$/ ]
def TARGET_PATTERN = "http://media.rozhlas.cz/_download/<identifier>.mp3"


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
		
        // Allow specifying a note which will be appended to the name of local file
        String[] parts = line.split(';', -1)
        line = parts[0]
        suffix = parts.length > 1 ? parts[1]: ""

        print suffix ? "Processing $line ($suffix)" : "Processing $line"

		for (def pattern : KNOWN_PATTERNS) {

    		def matcher = line =~  pattern
			
			// If current line matches a known pattern
			if (matcher.size() > 0) {
				def identifier = matcher[0][1]
				line = TARGET_PATTERN.replace("<identifier>", identifier);
				break
			}
        }

        suffix = suffix ? "_" + suffix : "";
        shortFileName = line.substring(line.lastIndexOf('/') + 1)
		if (shortFileName.indexOf('.') > -1) {
            target = shortFileName.substring(0, shortFileName.indexOf('.')) + suffix + shortFileName.substring(shortFileName.indexOf('.'));
        } else {
            target = shortFileName + suffix + ".mp3";
        }

        def targetFile = new File(WORKING_DIR + target);
		
		if (targetFile.exists()) {
			targetFile.delete();
		}

        HttpURLConnection connection = line.toURL().openConnection();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            println " ... returning HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
            throw new IOException()
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
