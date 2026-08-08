package resolvers;

import reflection.DataService;
import reflection.Services;

public class DataResolver {

    public static String resolve(String key, Object def) {
        return Services.get(DataService.class).get(key,def).toString();
    }
}
