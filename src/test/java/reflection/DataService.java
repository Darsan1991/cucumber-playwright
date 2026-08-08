package reflection;

import attributes.Service;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


@Service
public class DataService {
    public Map<String,Object> datas;
    
    public DataService() {

        try {
            var text = Files.readString(Path.of("src/test/resources/sample.json"));
            datas = new Gson().fromJson(text, Map.class);
        } catch (IOException e) {
            
        }
    }
    
    public Object get(String key,Object def) {
        return datas.getOrDefault(key,def);
    }
}
