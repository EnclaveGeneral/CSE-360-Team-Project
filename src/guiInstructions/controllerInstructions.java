package guiInstructions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.image.Image;

public class controllerInstructions {
	public controllerInstructions() {
		
	}
	
	public static ArrayList<ArrayList<Image>> get_images() {
        ArrayList<Image> list = new ArrayList<>();
        ArrayList<Image> list2 = new ArrayList<>();
        ArrayList<ArrayList<Image>> list3 = new ArrayList<>();

        File folder = new File("src/images");
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
                    String nameWithoutExt = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    try (FileInputStream fis = new FileInputStream(file)) {
                    	Image img = new Image(fis);
                    	if (nameWithoutExt.equals("back") || nameWithoutExt.equals("next")) {
                        	list2.add(img);
                        }
                    	else {
                    		list.add(img);
                    	}
                    	
                    } 
                    catch (IOException e) {
                        System.out.println("Failed to load image: " + file.getAbsolutePath());
                        e.printStackTrace();
                    }
                }
            }
        } 
        else {
            System.out.println("The folder does not exist or is not a directory: " + folder.getAbsolutePath());
        }

        list3.add(list);
        list3.add(list2);
        System.out.println(list3);
        return list3;
    }
}
