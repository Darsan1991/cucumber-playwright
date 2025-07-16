package reflection;


import attributes.Service;

@Service(type = CacheService.class)
public class CacheService {
    
    
    
    
    public void save(String key,Object value) {
        if (value == null) return;
    }

    /**
     * Hello world
     * @param key
     * @return
     */

    public Object get(String key) {
        return null;
    }
   
   public CacheService() {
        System.out.println("CacheService created");
    }
}
