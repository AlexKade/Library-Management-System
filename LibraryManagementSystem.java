
import java.io.*;
import java.util.*;


class Book implements Serializable
{   private static final long serialVersionUID = 1L;
  private final String title;
  private final String author;
  private final String isbn;
  private boolean isAvailable;

  public Book(String title, String author, String isbn)
  {
     this.title=title;
     this.author=author;
     this.isbn=isbn;
     this.isAvailable=true;
  }
  public String getTitle(){
      return title;
    }
  public String getAuthor(){
      return author;
    }
  public String getIsbn(){
      return isbn;
    }  
  public boolean isAvailable(){
      return isAvailable;
  }
  public void setAvailable(boolean available){
    isAvailable=available;
  }
  @Override
  public String toString(){
    return "Title:"+ title +", Author:" + author + ", ISBN:" + isbn +", Available:"+isAvailable;
  }

}
public class LibraryManagementSystem
{
    private static String filename="Library_records.txt";
    private Map<String, Book> bookbyisbn= new HashMap<>();
    private  Map<String, String> issuedbooks= new HashMap<>();
    
    public static void setname(String newname){
      filename=newname;
    }
    public LibraryManagementSystem(){
      loadbooksfromfile();
    }

    @SuppressWarnings("unchecked")
    private void loadbooksfromfile(){
      try (ObjectInputStream ois= new ObjectInputStream(new FileInputStream(filename))){
        bookbyisbn= (Map<String,Book>) ois.readObject();
        issuedbooks= (Map<String,String>) ois.readObject();
      } 
      catch(FileNotFoundException e){
        System.out.println("No records found, starting fresh");
      }
      catch(Exception e){
        System.out.println("Error occured"+e.getMessage());
      }
    }

    private void savebookstofile(){
      try(ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(filename))){
        oos.writeObject(bookbyisbn);
        oos.writeObject(issuedbooks);
      }
      catch(Exception e){
        System.out.println("Error in saving recods"+e.getMessage());
      }
    }

    public void addbook(String title, String author, String isbn){
      bookbyisbn.put(isbn, new Book(title,author,isbn));
      savebookstofile();
    }
   
    public void removebook(String isbn){
      if(bookbyisbn.remove(isbn) != null){
        savebookstofile();
        System.out.println("Book removed successfully");
      }
      else{System.out.println("No records found with ISBN:"+isbn);}
    }

    public void issuebook(String isbn, String username){
     Book book=bookbyisbn.get(isbn);
     if (book !=null && book.isAvailable()){
         book.setAvailable(false);
         issuedbooks.put(isbn, username);
         savebookstofile();
         System.out.println("Book issued to:"+username);
      }
      else if(book==null){
        System.out.println("No book with ISBN:"+ isbn+ " found in library");
      }
      else{
        System.out.println("Book not availiable");
      }
    }

    public void searchbook(String bookname){
      for(Book book:bookbyisbn.values()){
         if(book.getTitle().equalsIgnoreCase(bookname)||
            book.getAuthor().equalsIgnoreCase(bookname)||
            book.getIsbn().equalsIgnoreCase(bookname)){
              System.out.println(book);
            }
      }
    }

    public void returnbook(String isbn){
      Book book=bookbyisbn.get(isbn);
     if (book !=null && !book.isAvailable()){
         book.setAvailable(true);

         issuedbooks.remove(isbn);
         savebookstofile();
      }
      else if(book==null){
        System.out.println("No book with ISBN:"+ isbn+ " found in library");
      }
      else{
        System.out.println("Book not availiable");
      }
    }

    public void viewissuedbooks(){
      if(issuedbooks.isEmpty()){
        System.out.println("No books issued currently");
      }
      else{
        issuedbooks.forEach((isbn,username)->{
          Book book=bookbyisbn.get(isbn);
          String booktitle=(book!=null)? book.getTitle():"Uknown Title";
          System.out.println("ISBN:"+isbn+" Book:"+booktitle+" Issued to:"+username);
        });
      }
    }
    public void allbooks(){
      if(bookbyisbn.isEmpty()){System.out.println("No Books in library record");}
      else{
       System.out.println("All books in Libaray:");
       bookbyisbn.forEach((isbn,book)->{ System.out.println();
        System.out.println("ISBn:"+isbn+"->"+book);
       }); 
      }
     } 
     public void removeissuedbook(String isbn){
      if(issuedbooks.remove(isbn) != null){
        savebookstofile();
        System.out.println("Book removed successfully");
      }
      else{System.out.println("No records found with ISBN:"+isbn);}
     }
    public static void main(String[] args) 
    {
       LibraryManagementSystem library= new LibraryManagementSystem();
       Scanner scanner= new Scanner(System.in);


       while (true) {
        System.out.println("\n--- Library Management System ---");
        System.out.println("1. Add Book");
        System.out.println("2. Remove Book");
        System.out.println("3. Search Book");
        System.out.println("4. Issue Book");
        System.out.println("5. Return Book");
        System.out.println("6. View Issued Books");
        System.out.println("7. View all books");
        System.out.println("8. Remove from issued books");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1 -> {
                System.out.print("Enter title: ");
                String title = scanner.nextLine();
                System.out.print("Enter author: ");
                String author = scanner.nextLine();
                System.out.print("Enter ISBN: ");
                String isbn = scanner.nextLine();
                library.addbook(title, author, isbn);
               }
            case 2 -> {
                System.out.print("Enter ISBN to remove: ");
                   String isbn = scanner.nextLine();
                   library.removebook(isbn);
               }
            case 3 -> {
                System.out.print("Enter title, author, or ISBN to search: ");
                String query = scanner.nextLine();
                library.searchbook(query);
               }
            case 4 -> {
                System.out.print("Enter ISBN to issue: ");
                   String isbn = scanner.nextLine();
                   System.out.print("Enter user name: ");
                   String userName = scanner.nextLine();
                   library.issuebook(isbn, userName);
               }
            case 5 -> {
                System.out.print("Enter ISBN to return: ");
                   String isbn = scanner.nextLine();
                   library.returnbook(isbn);
               }
            case 6 -> library.viewissuedbooks();
            case 7 -> library.allbooks();
            case 8 -> {
              System.out.print("Enter ISBN to remove: ");
                 String isbn = scanner.nextLine();
                 library.removeissuedbook(isbn);
             }
            case 9 -> {
                System.out.println("Exiting system. Goodbye!");
                scanner.close();
                return;
               }
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }
  }

}