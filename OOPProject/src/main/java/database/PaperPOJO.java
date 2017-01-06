package database;

public class PaperPOJO {
	private int id;
	private String title;
	private String author;
	private String venue;
	private int year;
	private String content;
	
	public PaperPOJO(int id, String title, String author, String venue, int year, String content) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.venue = venue;
		this.year = year;
		this.content = content;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		System.out.println("Id"+getId()+" Title"+getTitle()+" Author"+getAuthor()+" Year"+getYear()+" Content"+getContent()+"");
		return super.toString();
	}
}
