package Factories;

import Repository.GMapsDataRepository;

public class DIFactory {

    public static GMapsDataRepository createMapsRepository(String tableName) {
        return new GMapsDataRepository(tableName);
    }
}
