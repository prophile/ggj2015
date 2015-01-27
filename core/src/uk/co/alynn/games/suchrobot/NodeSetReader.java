package uk.co.alynn.games.suchrobot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public abstract class NodeSetReader {
    public static NodeSet readNodeSet(String file) throws IOException {
        FileHandle handle = Gdx.files.external(file);
        if (handle.exists()) {
            System.err.println("USING OVERLOADED NODES");
        } else {
            handle = Gdx.files.internal(file);
        }
        InputStream rdr = handle.read();
        NodeSet nodes = new NodeSet();

        Scanner scan = new Scanner(rdr);
        scan.useLocale(Locale.UK);

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
