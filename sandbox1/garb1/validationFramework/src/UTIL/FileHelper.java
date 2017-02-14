/*
 * Created on Jul 8, 2005
 */

package util;

import java.io.File;

//this is utitlity class to make working with files more easy
//assumed for usage on testing report creation

public class FileHelper {
	public static void replaceFileWith(String oldfile, String newfile) {
		File newName = new File(oldfile);
		FileHelper.deleteFile(oldfile);
		File newFile = new File(newfile);
		newFile.renameTo(newFile);
	}
	
	public static void deleteFile(String filename) {
		File file = new File(filename);
		file.delete();
	}
}
