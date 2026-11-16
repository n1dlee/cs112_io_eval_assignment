package cs112_io_eval_assignment;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UncheckedIOException;
import java.io.ObjectOutputStream;

public class DoubleFileConverter {
    
    public static ArrayList<Double> readTextFile(String doubleTextFilename){
        ArrayList<Double> doubleList = new ArrayList<>();

        if(doubleTextFilename == null || doubleTextFilename.isEmpty()){
           throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        try(BufferedReader reader = new BufferedReader(new FileReader(doubleTextFilename))){
            String line;
            while((line = reader.readLine()) != null){
                try{
                    doubleList.add(Double.parseDouble(line));
                } catch(NumberFormatException e){
                    throw new IllegalArgumentException("Invalid double value: " + line, e);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading from file: " + doubleTextFilename, e);
        }

        return doubleList;
    }

    public static void writeTextFile(String doubleTextFilename, ArrayList<Double> doubles){
        // Implementation for writing to a text file

        if(doubleTextFilename == null || doubleTextFilename.isEmpty()){
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        if(doubles == null){
            throw new IllegalArgumentException("Doubles list cannot be null");
        }

        for(Double d : doubles){
            if(d == null){
                throw new IllegalArgumentException("Doubles list cannot contain null values");
            }
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(doubleTextFilename))){
            for(Double d : doubles){
                writer.write(d.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing to file: " + doubleTextFilename, e);
        }
    }
        

    public static ArrayList<Double> readBinaryFile(String doubleBinaryFilename){
        ArrayList<Double> doubleList = new ArrayList<>();
        
        if(doubleBinaryFilename == null || doubleBinaryFilename.isEmpty()){
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        // Узнать размер файла в байтах через File.lenth() и проверить остаток деления на 8.
        try(DataInputStream dis = new DataInputStream(new FileInputStream(doubleBinaryFilename))){
            long fileSize = new File(doubleBinaryFilename).length();
            if(fileSize % 8 != 0){
                throw new IllegalArgumentException("File size is not a multiple of 8 bytes, invalid binary double file");
            }
            long numDoubles = fileSize / 8;
            for(long i = 0; i < numDoubles; i++){
                doubleList.add(dis.readDouble());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading from file: " + doubleBinaryFilename, e);
        }

        return doubleList;
    }

    public static void writeBinaryFile(String doubleBinaryFilename, ArrayList<Double> doubles){
        // Implementation for writing to a binary file

        if(doubleBinaryFilename == null || doubleBinaryFilename.isEmpty()){
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        if(doubles == null){
            throw new IllegalArgumentException("Doubles list cannot be null");
        }

        for(Double d : doubles){
            if(d == null){
                throw new IllegalArgumentException("Doubles list cannot contain null values");
            }
        }

        try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(doubleBinaryFilename))){
            for(Double d : doubles){
                dos.writeDouble(d);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing to file: " + doubleBinaryFilename, e);
        }

    }

    public static ArrayList<Double> readBinaryListFile(String doubleBinaryListFilename){
        ArrayList<Double> doubleList = new ArrayList<>();
        
        if(doubleBinaryListFilename == null || doubleBinaryListFilename.isEmpty()){
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(doubleBinaryListFilename))){
            Object resultObject = ois.readObject();
            if (resultObject instanceof ArrayList) {
                ArrayList<?> tempList = (ArrayList<?>) resultObject;
                for (Object obj : tempList) {
                    if (!(obj instanceof Double)) {
                        throw new IllegalArgumentException("File contains non-Double values");
                    } 

                    doubleList.add((Double) obj);
                }
            } else {
                throw new IllegalArgumentException("File does not contain a valid ArrayList<Double>");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading from file: " + doubleBinaryListFilename, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found while reading from file: " + doubleBinaryListFilename, e);
        }

        return doubleList;
    }

    public static void writeBinaryListFile(String doubleBinaryListFilename, ArrayList<Double> doubles){
        // Implementation for writing to a binary list file

        if(doubleBinaryListFilename == null || doubleBinaryListFilename.isEmpty()){
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        if(doubles == null){
            throw new IllegalArgumentException("Doubles list cannot be null");
        }

        for(Double d : doubles){
            if(d == null){
                throw new IllegalArgumentException("Doubles list cannot contain null values");
            }
        }

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(doubleBinaryListFilename))){
            oos.writeObject(doubles);
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing to file: " + doubleBinaryListFilename, e);
        }

    }

}