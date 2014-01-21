package in.vasista.vbiz.datapop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.*;
import java.io.*;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/*
 * Utility class that reads CSV files (in a prescribed format) and spits out 
 * the xml that can then be fed to ofbiz to create a customer's master data
 */
public class SlurpCSV {

	final static private String IN_DATA_DIR = "/vasistautils/datapop/data/csv";
	final static private String OUT_DATA_DIR = "/vasistautils/datapop/data/xml";
	final static private String TEMP_FOLDER = "/runtime/tmp";
	final static private String TEMP_FOLDER_CSV = "/runtime/tmp/csv";
	final static private String TEMP_FOLDER_XML = "/runtime/tmp/xml";

	/**
	 * Private ctor
	 */
	private SlurpCSV() {
	}

	static String curDir = System.getProperty("user.dir");

	/**
	 * Main
	 * 
	 * @param args
	 *            args
	 */
	public static void main(String[] args) throws Exception {
		
		try{			
			processFiles(curDir+IN_DATA_DIR, OUT_DATA_DIR);
			
		}catch (Exception e) {
			 System.out.println(e.getMessage());
			 System.out.flush();
		}
	}

	/*
     * 
     */
	public static void processFiles(String inCsvDir, String outXmlDir)
	throws Exception {
		File inDataDir = new File(inCsvDir);
		FilenameFilter csvFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		};

		String[] inputFiles = inDataDir.list(csvFilter);
		if (inputFiles == null) {
			System.err.println("Data dir '" + inDataDir.getAbsolutePath()
					+ "' doesn't exist or no input \".csv\" files found");
			System.err.flush();
			throw new IOException("Data dir '" + inDataDir.getAbsolutePath()
					+ "' doesn't exist or no input \".csv\" files found");
		}
		for (int i = 0; i < inputFiles.length; i++) {
			processFile(new File(inDataDir + "/" + inputFiles[i]), outXmlDir);
		}
		
	}
	
	public static void processFile(File csvFile, String outXmlDir)
			throws Exception {
		System.out.println("SlurpCSV: Processing file '" + csvFile + "'");
		System.out.flush();
		BufferedReader buffReader = new BufferedReader(new FileReader(csvFile));
		String line;
		line = buffReader.readLine();
		if (line == null || line.isEmpty()) {
			System.err.println("Input file '" + csvFile + "' is empty");
			System.err.flush();
			throw new IOException("Input file '" + csvFile + "' is empty");
		}
		String[] nameTokens = line.split(",");
		String entityName = null;
		ArrayList<String> fieldNames = new ArrayList<String>();
		for (int i = 0; i < nameTokens.length; i++) {
			String entityField = nameTokens[i];
			int pos = entityField.indexOf('_');
			if (pos == -1) {
				System.err.println("Input file '" + csvFile
						+ "': entityField '" + entityField + "' missing _ ");
				System.err.flush();
				throw new IOException("Input file '" + csvFile
						+ "': entityField '" + entityField + "' missing _ ");
			}
			entityName = entityField.substring(0, pos);
			String fieldName = entityField.substring(pos + 1,
					entityField.length());
			fieldNames.add(fieldName);
		}
		ArrayList<ArrayList<String>> fieldValuesRows = new ArrayList<ArrayList<String>>();
		int rowNum = 1;
		while ((line = buffReader.readLine()) != null) {
			rowNum++;
			// System.out.println(line.length());

			ArrayList arr = preProcessEmbedCommas(line);
			line = (String) arr.get(0);
			Boolean flag = (Boolean) arr.get(1);			
			String[] valueTokens = line.split(",", -1);
			if (valueTokens.length == 0 || line.isEmpty()) {
				// for now we'll log and just skip this row. TODO can we do
				// better?
				System.out.println("row '" + rowNum + "' {" + line + "}: "
						+ "has empty values, skipping this row...");
				System.out.flush();
				continue;
			}
			String checkForWhiteSpace = line.trim();
			if (checkForWhiteSpace.isEmpty()) {
				// for now we'll log and just skip this row. TODO can we do
				// better?
				System.out.println("row '" + rowNum + "' {" + line + "}: "
						+ "has empty values, skipping this row...");
				System.out.flush();
				continue;
			}
			ArrayList<String> fieldValues = new ArrayList<String>();
			for (int i = 0; i < valueTokens.length; i++) {
				String tempField;
				if (flag.booleanValue()) {
					tempField = postProcessEmbedCommas(valueTokens[i]);

				} else{
					tempField = valueTokens[i];
				}
				fieldValues.add(tempField);
			}
			// sanity check
			if (fieldNames.size() != fieldValues.size()) {
				System.err.println("row '" + rowNum + "' {" + line + "}: "
						+ "fieldNames (" + fieldNames.size()
						+ ") and fieldValues (" + fieldValues.size()
						+ ") - size mismatch " + line.length());
				System.err.flush();
				throw new IOException("While processing File  '"
						+ csvFile.getName() + "' :: row '" + rowNum + "' {"
						+ line + "}: " + "fieldNames (" + fieldNames.size()
						+ ") and fieldValues (" + fieldValues.size()
						+ ") - size mismatch " + line.length());
			}

			fieldValuesRows.add(fieldValues);
		}
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("entity-engine-xml");
		for (int i = 0; i < fieldValuesRows.size(); ++i) {
			Element entity = root.addElement(entityName);
			ArrayList<String> fieldValues = fieldValuesRows.get(i);
			for (int j = 0; j < fieldNames.size(); j++) {
				entity.addAttribute(fieldNames.get(j), fieldValues.get(j));
			}
		}
		int dotIdx = csvFile.getName().lastIndexOf(".");
		String inFileName = csvFile.getName().substring(0, dotIdx);
		String outFileName = inFileName + "Data.xml";
		createXmlFile(doc, csvFile, outFileName, outXmlDir);
	}

	static void createXmlFile(Document doc, File csvFile, String outFileName,
			String outXmlDir) throws Exception {
		int dotIdx = csvFile.getName().lastIndexOf(".");
		String inFileName = csvFile.getName().substring(0, dotIdx);
		String runLevel = "1";
		int idx = inFileName.lastIndexOf("_");
		if (idx >= 0) {
			String tmp = inFileName.substring(++idx);
			try {
				int level = Integer.parseInt(tmp);
				if (level < 1 || level > 10) {
					System.err.println("Run level has to be between 1 and 10 {"
							+ csvFile + "}");
					System.err.flush();
				}
				runLevel = tmp;
			} catch (NumberFormatException e) {
				// do nothing, assume 1st run level
			}
		}
		File outParentDir = new File(curDir + outXmlDir);
		if (!outParentDir.exists()) {
			outParentDir.mkdir();
		}
		File outDir = new File(outParentDir.getPath() + "/" + runLevel);
		if (!outDir.exists()) {
			// let's create the dir
			if (!outDir.mkdir()) {
				System.err.println("mkdir failed {" + outDir + "}");
				System.err.flush();
			}
		}
		String outFile = outDir + "/" + outFileName;
		FileWriter out = new FileWriter(outFile);
		OutputFormat format = new OutputFormat("    ", true);
		XMLWriter writer = new XMLWriter(out, format);
		writer.write(doc);
		writer.flush();
		System.out.println("SlurpCSV: Created file '" + outFile + "'");
		System.out.flush();
	}

	static ArrayList preProcessEmbedCommas(String line) {
		int charNum = 0;
		Boolean flag =Boolean.FALSE;
		StringBuffer newline = new StringBuffer();
		charNum = line.indexOf('"');
		if (charNum != -1) {
			newline.append(line.substring(0, charNum));
			while (charNum < line.length()) {
				// Get current character from string
				newline.append(line.charAt(charNum));
				int firstIndex = 0;
				if (line.charAt(charNum) == '"') {
					firstIndex = charNum;
					for (int i = charNum + 1; i < line.length(); i++) {
						if (line.charAt(i) == '"') {
							String tempStr = line.substring(firstIndex + 1,
									i + 1);
							tempStr = tempStr.replace(",", "^");
							if (!tempStr.equals(line.substring(firstIndex + 1, i + 1))){
								flag = Boolean.TRUE;								
							}
							newline.append(tempStr);
							charNum = i;
							break;
						}
					}
					charNum++;
					continue;
				}

				charNum++;
			}
			line = newline.toString();
		}
		ArrayList result = new ArrayList();
		result.add(line);
		result.add(flag);
		return result;
	}

	static String postProcessEmbedCommas(String valueTokens) throws Exception {
		String tempField;
		tempField = valueTokens;		
		if ((valueTokens.indexOf('^')) != -1) {
			tempField = valueTokens.replace('^', ',');
			
		}
		String finalToken = "";
		for (int k = 0; k < tempField.length(); k++) {
			if (tempField.charAt(k) == '"') {
				if (k < (tempField.length() - 1)) {
					if (tempField.charAt(k + 1) == '"')
						finalToken += tempField.charAt(k);

				}
			}

			if (tempField.charAt(k) != '"'){
				finalToken += tempField.charAt(k);
			}

		}
		return finalToken;
	}

	// un ziping files
	private static final String FILESEPARATOR = File.separator;

	public static void storeZipStream(InputStream inputStream, String dir)
			throws IOException {

		ZipInputStream zis = new ZipInputStream(inputStream);
		ZipEntry entry = null;
		int countEntry = 0;
		if (!dir.endsWith(FILESEPARATOR))
			dir += FILESEPARATOR;

		// check inputStream is ZIP or not
		if ((entry = zis.getNextEntry()) != null) {
			do {
				String entryName = entry.getName();
				// Directory Entry should end with FileSeparator
				if (!entry.isDirectory()) {
					// Directory will be created while creating file with in it.
					String fileName = dir + entryName;
					createFile(zis, fileName);
					countEntry++;
				}
			} while ((entry = zis.getNextEntry()) != null);
			System.out.println("No of files Extracted : " + countEntry);
			System.out.flush();

		} else {

			throw new IOException("Given file is not a Compressed one");

		}
	}

	public static void createFile(InputStream is, String absoluteFileName)
			throws IOException {
		File f = new File(absoluteFileName);

		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		OutputStream out = new FileOutputStream(absoluteFileName);
		byte[] buf = new byte[1024];
		int len = 0;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		// Close the streams
		out.close();
	}

	public static String unzipCsv(String zipFileLocation, String outputFolder)
			throws Exception {
		String dir = System.getProperty("java.io.tmpdir");
		FileInputStream zis = null;
		File zipFile = null;
		try {
			zipFile = new File(zipFileLocation);
			zis = new FileInputStream(zipFile);

			if (!outputFolder.isEmpty()) {
				dir = outputFolder.replace('\\', '/');
			}
			System.out.println("Extracted to " + dir);
			System.out.flush();
			storeZipStream(zis, curDir + dir);			

		} catch (Exception e) {
			return e.getMessage();
		} finally {
			zis.close();
		}
		return dir;
	}

	public static String unzip(String inFileName) throws Exception {
		String errorMsg = "";
		boolean flag = false;
		try {
			String csvFilesLocation = unzipCsv(curDir + "/" + inFileName,
					TEMP_FOLDER_CSV);
			File inDataDir = new File(curDir + csvFilesLocation);
			FilenameFilter csvFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".csv");
				}
			};

			File[] inDataDirList = inDataDir.listFiles();
			for (int j = 0; j < inDataDirList.length; j++) {
				if (inDataDirList[j].isDirectory()) {
					inDataDir = inDataDirList[j];
				}
				processFiles(inDataDir.getAbsolutePath(), TEMP_FOLDER_XML);				
			}

			File inFolder = new File(curDir + TEMP_FOLDER_XML);
			String files[] = inFolder.list();
			zipDir("out.zip", inFolder.getAbsolutePath());

		} catch (Exception e) {
			// TODO: handle exception
			return e.getMessage();
		}

		return "success";
	}

	private static void zipDir(String zipFileName, String dir) throws Exception {
		File dirObj = new File(dir);
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(curDir
				+ TEMP_FOLDER + "/" + zipFileName));
		System.out.println("Creating : " + zipFileName);
		System.out.flush();
		addDir(dirObj, out);
		out.close();
	}

	static void addDir(File dirObj, ZipOutputStream out) throws IOException {
		File[] files = dirObj.listFiles();

		byte[] tmpBuf = new byte[1024];

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				addDir(files[i], out);				
				continue;
			}
			FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
			String entryName = files[i].getAbsolutePath();
			out.putNextEntry(new ZipEntry(entryName.substring(
					entryName.lastIndexOf("xml/"), entryName.length())));
			int len;
			while ((len = in.read(tmpBuf)) > 0) {
				out.write(tmpBuf, 0, len);
			}
			out.closeEntry();
			in.close();			
		}
		
	}
}
