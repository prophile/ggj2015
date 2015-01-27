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
        Scanner globalScan = scan.useLocale(Locale.UK);

        while (scan.hasNext()) {
            String command = globalScan.next();
            if (command.equals("node")) {
                String type = globalScan.next();
                String name = globalScan.next();
                int x = globalScan.nextInt();
                int y = globalScan.nextInt();
                int reserves = globalScan.nextInt();
                nodes.addNode(type, name, x, y, reserves);
            } else if (command.equals("conn")) {
                String source = globalScan.next();
                String dest = globalScan.next();
                nodes.addConnection(source, dest);
            }
        }

        scan.close();

        nodes.compile();
        return nodes;
    }
}
