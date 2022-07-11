import org.jnbt.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class JSONtoDAT {
    static File JSONText;
    static BufferedReader buf;


    public static ShortTag createShortTag(String tagName, String value){
        return new ShortTag(tagName,Short.parseShort(value));
    }

    public static LongTag createLongTag(String tagName, String value){
        return new LongTag(tagName,Long.parseLong(value));
    }

    public static FloatTag createFloatTag(String tagName, String value){
        return new FloatTag(tagName,Float.parseFloat(value));
    }

    public static IntTag createIntTag(String tagName, String value){
        return new IntTag(tagName,Integer.parseInt(value));
    }

    public static DoubleTag createDoubleTag(String tagName, String value){
        return new DoubleTag(tagName,Double.parseDouble(value));
    }

    public static ByteTag createByteTag(String tagName, String value){
        return new ByteTag(tagName,Byte.parseByte(value));
    }

    public static ByteArrayTag createByteArrayTag(String tagName, String value) {
        int byteCount = 0;
        for(Character c : value.toCharArray()){
            if(c == '0' || c == '1')
                byteCount++;
        }

        byte[] byteArray = new byte[byteCount];
        byteCount = 0;
        for(Character c : value.toCharArray()){
            if(c == '0' || c == '1')
                byteArray[byteCount++] = Byte.parseByte(c.toString());
        }

        return new ByteArrayTag(tagName,byteArray);
    }

    public static StringTag createStringTag(String tagName, String value) throws IOException{
        String line = null;

        value = value.trim();
        do{
            if(line != null)
               value +=  "\n"+line;
            if((value.endsWith("\"") && !value.endsWith("\\\""))||(value.endsWith("\",") && !value.endsWith("\\\","))){
                value = value.substring(1,value.lastIndexOf("\""));
                value = value.replace("\\\"","\"");
                if(value.equals("\"\""))
                    value = "";
                return new StringTag(tagName,value);
            }
        }
        while((line = buf.readLine()) != null);

        return null;
    }

    public static ListTag createListTag(String tagName) throws IOException {
        ArrayList<Tag> tagList = new ArrayList<>();

        String line = "";
        while((line = buf.readLine()) != null) {
            line = line.trim();

            if(line.endsWith("]") || line.endsWith("],")) {
                if(tagList.size() > 0)
                    return new ListTag(tagName, tagList.get(0).getClass(), tagList);
                return new ListTag(tagName, EndTag.class, tagList);
            }

            tagList.add(createTag(line));
        }
        return null;
    }

    public static CompoundTag createCompoundTag(String tagName) throws IOException{
        Map<String, Tag> compoundMap = new HashMap<>();

        String line = "";
        while((line = buf.readLine()) != null) {
            line = line.trim();

            if(line.endsWith("}") || line.endsWith("},")) {
                return new CompoundTag(tagName, compoundMap);
            }

            compoundMap.put(getTagName(line),createTag(line));
        }
        return null;
    }

    public static String getTagValue(String line){
        //Gets tag value
        if(line.contains("\":")) {
            return line.substring(line.indexOf(':')+1);
        }
        return line;
    }
    public static String getTagName(String line){
        //Gets tag name
        String tagName = "";
        if(line.contains("\":")) {
            tagName = line.substring(0, line.indexOf(':'));
            tagName = tagName.replace("\"","");
        }
        return tagName;
    }
    public static Tag createTag(String line) throws IOException {
        String tagName = getTagName(line);
        String value = getTagValue(line);

        if(line.contains(": {") || line.equals("{")){
            return createCompoundTag(tagName);
        }
        if(line.endsWith(": [") || line.equals("[")) {
            return createListTag(tagName);
        }
        if(line.contains("[") && (line.endsWith("]") || line.endsWith("],"))){
            return createByteArrayTag(tagName,value);
        }

        if(value.trim().replace(",", "").replaceAll("[^\\d.]", "").matches("-?\\d+(\\.\\d+)?") && !line.contains("\": \"") && !value.startsWith("\"")) {
            value = value.trim().replace(",", "");
            if (value.contains("f"))
                return createFloatTag(tagName, value.replace("f", ""));
            if (value.contains("d"))
                return createDoubleTag(tagName, value.replace("d", ""));
            if (value.contains("b"))
                return createByteTag(tagName, value.replace("b", ""));
            if (value.contains("s"))
                return createShortTag(tagName, value.replace("s", ""));
            if (value.contains("L"))
                return createLongTag(tagName, value.replace("L", ""));

            return createIntTag(tagName, value);
        }

//        if(line.contains("\": \"")){
//        if(line.startsWith("\"") || line.contains("\": \"")){
        return createStringTag(tagName,value);

 //       return null;
    }

    public static void main(String[] args) {
        try {
            JSONText = new File(args[0]);
            FileReader JSONFileReader = new FileReader(JSONText);
            buf = new BufferedReader(JSONFileReader);

            buf.readLine();
            CompoundTag rootTag = createCompoundTag("");

            NBTOutputStream nbtOutputStream = new NBTOutputStream(new FileOutputStream(args[1]));
            nbtOutputStream.writeTag(rootTag);
            nbtOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}