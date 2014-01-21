/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package in.vasista.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sindukur
 */
public class FileUtil {

    public static List<File> listFiles(
            File directory,
            String filterFileName,
            boolean recurse) {
      return   listFiles(directory,getFilenameFilterForFullName(filterFileName),recurse);
    }

    public static List<File> listFiles(
            File directory,
            FilenameFilter filter,
            boolean recurse) {

        List<File> files = new ArrayList<File>();

        File[] entries = directory.listFiles();


        for (File entry : entries) {

            if (filter == null || filter.accept(directory, entry.getName())) {
                files.add(entry);
            }

            if (recurse && entry.isDirectory()) {
                files.addAll(listFiles(entry, filter, recurse));
            }
        }

        return files;
    }






    public static FilenameFilter getFilenameFilterForSufix(final String fileName)
            {
        FilenameFilter ret=new FilenameFilter() {
           public boolean accept(File dir, String name) {
                return name.endsWith(fileName);
            }
      };
      return ret;
      
    }

public static FilenameFilter getFilenameFilterForFullName(final String fileName)
            {
        FilenameFilter ret=new FilenameFilter() {
           public boolean accept(File dir, String name) {
                return name.equals(fileName);
            }
      };
      return ret;

    }
}
