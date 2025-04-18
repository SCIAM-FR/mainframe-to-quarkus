package com.sciam.ingestion;

import com.sciam.ingestion.model.Error;
import com.sciam.ingestion.model.Record;
import com.sciam.ingestion.parser.StoreParser;
import io.vavr.control.Either;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class App {

    public static void main(String[] args) throws IOException {
        StoreParser storeParser = new StoreParser("DTAR020.bin");
        Either<Error, List<Record>> records = storeParser.parse();
        String message = records.fold(left -> {
                    System.out.println(left);
                    return "There is an error ";
                }
                , right -> {
                    System.out.println(right);
                    return "Every thing is ok";
                }
        );

        // Write the content of record into a file
        List<String> lines = records
                .get()
                .stream()
                .map(p -> String.format("%s;%s;%s;%s", p.number(), p.date(), p.price(), p.sold()))
                .toList();
        Files.write(Paths.get("records.txt"), lines, StandardOpenOption.CREATE);

        System.out.println(message);
    }
}
