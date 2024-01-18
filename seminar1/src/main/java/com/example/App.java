package com.example;
import java.util.*;
import java.util.stream.*;
import java.util.stream.Collectors.*;

public class App
{
    public static void main( String[] args )
    {
        System.out.print(
                "Exercise 1: Count how many times does each word appear in an array.\n" +
                "    a. Procedural solution: Use a HashMap and a loop, don’t use functional programming.\n" +
                "    b. Procedural solution: Use a stream and a collector, use functional programming.\n" +
                "\n" +
                "Exercise 2: Counting people. Given an array of Person (each with a name, city and language), count how many\n" +
                "people speak english in each city. The language should be case-insensitive (i.e. “english” and\n" +
                "“English” and “EnGlIsH” are the same).\n" +
                "   a. Procedural solution: Use a HashMap and a loop, don’t use functional programming.\n" +
                "   b. Functional solution: Use a stream and a collector, use functional programming.\n");
        Scanner sc = new Scanner(System.in);
        System.out.print("Select exercise: ");
        int ex = sc.nextInt();
        sc.nextLine(); // consume newline

        /*
        Exercise 1: Count how many times does each word appear in an array.
            a. Procedural solution: Use a HashMap and a loop, don’t use functional programming.
            b. Procedural solution: Use a stream and a collector, use functional programming.

        Exercise 2: Counting people. Given an array of Person (each with a name, city and language), count how many
        people speak english in each city. The language should be case-insensitive (i.e. “english” and
        “English” and “EnGlIsH” are the same).
            a. Procedural solution: Use a HashMap and a loop, don’t use functional programming.
            b. Functional solution: Use a stream and a collector, use functional programming.
        */
        switch (ex) {
            case 1: {
                System.out.println("Input words of the array separated by spaces: ");
                String raw = sc.nextLine();
                String[] arr = raw.split(" ");

                System.out.println("Type 'a' for procedural solution and 'b' for functional solution");
                char soltype = sc.next().charAt(0);
                sc.nextLine();

                switch (soltype) {
                    case 'a': {
                        double start = System.currentTimeMillis();
                        Map<String, Integer> wordcount = new HashMap<>();
                        for (String key : arr) {
                            if (!wordcount.containsKey(key)) {
                                wordcount.put(key, 1);
                            } else {
                                wordcount.put(key, wordcount.get(key) + 1);
                            }
                        }
                        double stop = System.currentTimeMillis();
                        System.out.println(wordcount);
                        System.out.printf("Elapsed time: %fms%n", stop - start);
                        break;
                    }
                    case 'b': {
                        double start = System.currentTimeMillis();
                        Stream<String> wordStream = Arrays.stream(arr);
                        Map<String, Long> wordcount = wordStream.collect(
                                Collectors.groupingBy(
                                        k -> k,
                                        Collectors.counting()
                                )
                        );
                        double stop = System.currentTimeMillis();
                        System.out.println(wordcount);
                        System.out.printf("Elapsed time: %fms%n", stop - start);
                        break;
                    }
                }
                break;
            }
            case 2: {
                System.out.println("Input people (Name City Language) separated by spaces, each person by period.\n" +
                        "(i.e.: Luca Barcelona Italian. Maria Barcelona Spanish): ");
                String raw = sc.nextLine();
                raw = raw.toLowerCase();
                String[] temp = raw.split("\\. ");
                List<Person> arr = new ArrayList<>();
                for (String s : temp) {
                    arr.add(new Person(s.split(" ")));
                }

                System.out.println("Type 'a' for procedural solution and 'b' for functional solution");
                char soltype = sc.next().charAt(0);
                sc.nextLine();

                switch (soltype) {
                    case 'a': {
                        double start = System.currentTimeMillis();
                        Map<String, Integer> peoplecount = new HashMap<>();
                        for (Person p : arr) {
                            CityLang tempkey = new CityLang(p.City, p.Language);
                            String key = tempkey.toString();
                            if (!peoplecount.containsKey(key)) {
                                peoplecount.put(key, 1);
                            } else {
                                peoplecount.put(key, peoplecount.get(key) + 1);
                            }
                        }
                        double stop = System.currentTimeMillis();
                        System.out.println(peoplecount);
                        System.out.printf("Elapsed time: %fms%n", stop - start);
                        break;
                    }
                    case 'b': {
                        double start = System.currentTimeMillis();
                        Stream<Person> peopleStream = arr.stream();
                        Map<CityLang, Long> peoplecount = peopleStream.collect(
                                Collectors.groupingBy(
                                        Person::getCityLanguage,
                                        Collectors.counting()
                                )
                        );
                        double stop = System.currentTimeMillis();
                        System.out.println(peoplecount);
                        System.out.printf("Elapsed time: %fms%n", stop - start);
                        break;
                    }
                }
                break;
            }
            case 3:
                System.out.println("(3) Not implemented");
                break;
        }
    }
}

class Person {
    public String Name, City, Language;
    public Person(String[] args) {
        Name = args[0];
        City = args[1];
        Language = args[2];
    }

    public CityLang getCityLanguage() {
        return new CityLang(City, Language);
    }
}

class CityLang {
    public String City, Language;
    public CityLang(String city, String lang) {
        City = city;
        Language = lang;
    }

    @Override
    public String toString() {
        return City+"-"+Language;
    }
}