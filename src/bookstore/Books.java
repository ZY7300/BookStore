/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore;

/**
 *
 * @author Zi Yu
 */
public class Books {

    private int id;
    private String title;
    private String author;
    private int year;
    private Double price;
    private byte[] image;

    /**
     * This is Books constructor that initializeS Books object with specified attributes
     *
     * @param id
     * @param title
     * @param author
     * @param year
     * @param price
     * @param image
     */
    protected Books(int id, String title, String author, int year, Double price, byte[] image) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
        this.image = image;
    }

    /**
     * This method get the unique identifier of the book
     *
     * @return unique identifier of the book
     */
    public int getId() {
        return id;
    }

    /**
     * This method get the title of the book
     *
     * @return title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method get the author of the book
     *
     * @return author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * This method get the publish year of the book
     *
     * @return publish year of the book
     */
    public int getYear() {
        return year;
    }

    /**
     * This method get the price of the book
     *
     * @return price of the book
     */
    public Double getPrice() {
        return price;
    }

    /**
     * This method get the cover image of the book
     *
     * @return cover image of the book
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * This method set the unique identifier of the book
     *
     * @param id 
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * This method set the title of the book
     *
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This method set the author of the book
     * 
     * @param author 
     */
    public void setAuthor(String author) {
        this.author = author;
    }
    
    /**
     * This method set the publish year of the book
     * 
     * @param year 
     */
    public void setYear(int year) {
        this.year = year;
    }
    
    /**
     * This method set the price of the book
     * 
     * @param price 
     */
    public void setPrice(Double price) {
        this.price = price;
    }
    
    /**
     * This method set the cover image of the book
     * 
     * @param image 
     */
    public void setImage(byte[] image) {
        this.image = image;
    }
}
