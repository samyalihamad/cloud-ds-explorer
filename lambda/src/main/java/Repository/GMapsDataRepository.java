package Repository;

import Interfaces.MapsRepository;
import Models.Point;
import Models.PointEdge;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class GMapsDataRepository implements MapsRepository {
    private final AmazonDynamoDB client;
    private final DynamoDB dynamoDB;
    private final Table table;

    public GMapsDataRepository(String tableName) {
        this.client = AmazonDynamoDBClientBuilder.defaultClient();
        this.dynamoDB = new DynamoDB(client);
        this.table = dynamoDB.getTable(tableName);
    }

    @Override
    public Point getPoint(int x, int y) {
        Item item = table.getItem("point", x + "," + y);
        if(item != null) {
            return new Point(x, y);
        }

        return null;
    }

    @Override
    public void savePoint(Point point, List<PointEdge> edges) {

        Item item = new Item()
                .withPrimaryKey("point", point.x + "," + point.y)
                .with("quadId", point.quadId)
                .withList("edges", edges.stream().map((edge) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("point", edge.point.x + "," + edge.point.y);
                    map.put("weight", edge.weight);
                    return map;
                }).collect(Collectors.toList()));

        table.putItem(item);
    }
}
