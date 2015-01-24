package uk.co.alynn.games.suchrobot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;

public abstract class NodeSetReader {
    public static NodeSet readNodeSet(String file) throws IOException {
        InputStream rdr = Gdx.files.internal(file).read();
        NodeSet nodes = new NodeSet();

        Scanner scan = new Scanner(rdr);

        while (scan.hasNext()) {
            String command = scan.next();
            if (command.equals("node")) {
                String type = scan.next();
                String name = scan.next();
                int x = scan.nextInt();
                int y = scan.nextInt();
                int reserves = scan.nextInt();
                nodes.addNode(type, name, x, y, reserves);
            } else if (command.equals("conn")) {
                String source = scan.next();
                String dest = scan.next();
                nodes.addConnection(source, dest);
            }
        }

        scan.close();

        nodes.compile();
        return nodes;
    }
}
