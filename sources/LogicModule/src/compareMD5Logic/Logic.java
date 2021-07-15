package compareMD5Logic;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Logic {

    public Map<String, String> getListOfFiles(Path path) throws IOException {

        Map<String, String> fileList = new HashMap<>();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                if (!Files.isDirectory(file)) {

                    try {
                        if (!file.toFile().getName().equals("listOfFiles.ser"))
                            fileList.put(file.toFile().getName(), getFileChecksum(file.toFile()));
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                }
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }


    private static String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException {

        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);

        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            md5Digest.update(byteArray, 0, bytesCount);
        }


        fis.close();
        byte[] bytes = md5Digest.digest();

        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();

    }

    public void saveDirectoryState(Path path, Map<String, String> filesInFolder) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(path + "/listOfFiles.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(filesInFolder);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public Map<String, String> getDirectoryStatus(File file) {
        Map<String, String> listOfFiles = null;
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            listOfFiles = (Map<String, String>)  in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
            return null;
        }
        return listOfFiles;
    }

    public boolean areEqual(Map<String, String> first, Map<String, String> second) {
        if (first.size() != second.size()) {
            return false;
        }

        return first.entrySet().stream()
                .allMatch(e -> e.getValue().equals(second.get(e.getKey())));
    }

}
