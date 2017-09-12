/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.RecommendationSystem;

/**
 *
 * @author Will
 */
public class GenreMap {
    private ArrayList<Genre> genres;
    private int count;
    private Map<String, Integer> genreMap = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
    private int[][] genreMatrix;
    private boolean init;
    private final String genrePath;
    private final String relationPath;
    
    public GenreMap(String path){
        genrePath = path + "genre_names.rcm";
        relationPath = path + "genre_rels.rcm";
        loadMap();
    }
    
    public void addGenre(String g) {
        Genre newGenre = new Genre(g);
        genres.add(newGenre);
        genreMap.put(g, count);
        count++;
    }
    
    public void addRel(String g1, String g2, int weight){
        if(!init){
//            System.out.println("There are "+count+" genres");
            genreMatrix = new int[count][count];
            for(int i = 0; i < count; i++){
                for(int r = 0; r < count; r++){
                    genreMatrix[i][r] = 10;
                }
            }
            init = true;
        }
        genreMatrix[genreMap.get(g1)][genreMap.get(g2)] = weight;
        genreMatrix[genreMap.get(g2)][genreMap.get(g1)] = weight;
    }
    
    public Genre getGenre(int i, int w){
        Genre genre = genres.get(i);
        genre.setWeight(w);
        return genre;
    }
    public Genre getGenre(String g, int w){
        return getGenre(genreMap.get(g), w);
    }
    public Genre getGenre(String g){
        return getGenre(genreMap.get(g), 0);
    }
    
    public ArrayList<Genre> getLowestGenres(Genre startGenre, ArrayList<Genre> visited){
        int pos = genreMap.get(startGenre.getName());
        for(int i = 0; i < genreMatrix[pos].length; i++){ // iterates through each of the relations the start node has
            if(genreMatrix[pos][i] != 10){ // if there is a connection between the two nodes
                if(!visited.contains(genres.get(i))){
                    visited.add(getGenre(i, genreMatrix[pos][i] + startGenre.getWeight()));
                }
            }
        }
        Collections.sort(visited);
        return visited;
    }
    
    public boolean exists(String genreToCheck){
        for(Genre genre : genres){
            if(genreToCheck.equalsIgnoreCase(genre.getName())){
                return true;
            }
        }
        return false;
    }
    
    public String addNewGenre(String newGenre) throws IOException{
        FileWriter writer = new FileWriter(relationPath, true);
        PrintWriter printer = new PrintWriter(writer);
        String[] commonGenres = {"Rock","Alt","Rap","Hip","Hop","Funk","Jazz","Blue","Dance","Pop","Metal","Elect","Soul"};
        ArrayList<String> keywords = new ArrayList<>();

        String replace = newGenre.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", " ");
        String words[] = replace.split(" ");
        for(String w : words){
            keywords.add(w);
        }
        for(String s : commonGenres){
            if(newGenre.contains(s)){
                if(!keywords.contains(s)){
                    keywords.add(s);
                }
            }
        }
        ArrayList<Genre> similar = new ArrayList<>();
        for(Genre genre : genres){
            for(String key : keywords){
                if(genre.getName().contains(key)){
                    if(!similar.contains(genre)){
                        similar.add(genre);
                    }
                }
            }
        }
        if(similar.size() > 0){
            System.out.printf("These genres seem similar to %s:\n",newGenre);
            for(Genre genre : similar){
                System.out.print(genre.getName()+"\t");
            }
            System.out.print("\n\n");
        }
        boolean complete = false;
        int count = 0;

        Scanner scan = new Scanner(System.in);

        while(!complete){
            complete = false;
            System.out.println("Type complete to move to next genre / or discard or quit");
            System.out.printf("Type a similar genre to %s: ",newGenre);
            String input = scan.nextLine();
            if(exists(input)){
                String genre = input;
                boolean weighted = false;
                do {
                    System.out.print("The similarity of the two genres 0 - 9: quit to cancel: ");
                    input = scan.next();
                    if(input.matches("[0-9]")){
                        int weight = Integer.parseInt(input);
                        System.out.printf("%s--%s--%d%n", newGenre,  genre, weight);
                        printer.printf("%s--%s--%d%n", newGenre, genre, weight);
                        printer.flush();
                        count++;
                        weighted = true;
                    } else if(input.equals("quit")){
                        System.out.printf("Cancelling similarity between %s and %s%n",input,newGenre);
                        weighted = true;
                    } else System.out.println("You didn't enter a number between 0 - 9");
                } while(!weighted);
            } else if(input.equals("complete") | input.equals("c")){
                System.out.printf("Adding %s to File with %d relationships%n",newGenre,count);
                if(count > 0){
                    FileWriter nameWriter = new FileWriter("xml/genre_names.rcm", true);
                    PrintWriter namePrinter = new PrintWriter(nameWriter);
                    namePrinter.printf("%s%n",newGenre);
                    namePrinter.close();
                    count = 0;
                }
                complete = true;
            } else if(input.equals("discard") | input.equals("d")){
                System.out.println("Discarding "+newGenre);
                return newGenre;
            } else if(input.equals("quit")) break;
            else System.out.println("That Genre does not exist");

        }
        return null;
    }
    
    public ArrayList<String> addMultipleGenres(ArrayList<String> newGenres) throws IOException{
        System.out.printf("There are %s genres that need to be added%n",newGenres.size());
        ArrayList<String> discardedGenres = new ArrayList<>();
        for(String newGenre : newGenres){
            String discard = addNewGenre(newGenre);
            if(discard != null){
                if(!discardedGenres.contains(discard)){
                    discardedGenres.add(discard);
                }
            }
        }
        loadMap();
        return discardedGenres;
        
    }
    
    public void saveGenres(){
        try {
            FileWriter writer = new FileWriter(genrePath, false);
            PrintWriter printer = new PrintWriter(writer);
            for(Genre genre : genres){
                printer.printf("%s%n", genre.getName());
            }
            printer.close();
            printer.flush();
            writer = new FileWriter(relationPath, false);
            printer = new PrintWriter(writer);
            for(int i = 0; i < genreMatrix.length; i++){
                for(int r = 0; r < genreMatrix.length; r++){
                    if(genreMatrix[i][r] < 10){
                        printer.printf("%s--%s--%d%n", genres.get(i).getName(), 
                                       genres.get(r).getName(), genreMatrix[i][r]);
                    }
                }
            }
            printer.close();
        } catch (IOException ex) {
            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public ArrayList<Genre> getGenres() {
        return genres;
    }
    public void loadMap(){
        try {
            Scanner scan = new Scanner(new FileInputStream(genrePath));
        
        this.genres = new ArrayList<>();
        this.count = 0;
        this.genreMap = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        this.init = false;
        while(scan.hasNext()){
            addGenre(scan.nextLine());
        }
        scan = new Scanner(new FileInputStream(relationPath));
        while(scan.hasNext()){
            String[] split = scan.nextLine().split("--");
            addRel(split[0],split[1],Integer.parseInt(split[2]));
        }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenreMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
