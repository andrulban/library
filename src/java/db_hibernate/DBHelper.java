package db_hibernate;

import entity_hibernate.Author;
import entity_hibernate.Book;
import entity_hibernate.Genre;
import entity_hibernate.HibernateUtil;
import entity_hibernate.Publisher;
import java.util.List;
import jbeans.Pager;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

/**
 *
 * @author andrusha
 */
public class DBHelper {

    private SessionFactory sessionFactory = null;
    private DetachedCriteria bookListCriteria;
    private DetachedCriteria booksCountCriteria;
    private Pager pager = Pager.getInstace();
    private ProjectionList bookProjection;
    private static DBHelper dBHelper;

    private DBHelper() {
        prepareCriterias();
        sessionFactory = HibernateUtil.getSessionFactory();

        bookProjection = Projections.projectionList();
        bookProjection.add(Projections.property("id"), "id");
        bookProjection.add(Projections.property("name"), "name");
        bookProjection.add(Projections.property("image"), "image");
        bookProjection.add(Projections.property("genre"), "genre");
        bookProjection.add(Projections.property("pageCount"), "pageCount");
        bookProjection.add(Projections.property("isbn"), "isbn");
        bookProjection.add(Projections.property("publisher"), "publisher");
        bookProjection.add(Projections.property("author"), "author");
        bookProjection.add(Projections.property("publishYear"), "publishYear");
        bookProjection.add(Projections.property("descr"), "descr");
        //bookProjection.add(Projections.property("content"), "content");
        populateList();
    }
    
    public static DBHelper getInstance() {
        if(dBHelper==null) {
            return dBHelper=new DBHelper();
        }
        else {
            return dBHelper;
        }
    }

    

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<Genre> getAllGenres() {
        return getSession().createCriteria(Genre.class).list();
    }

    public List<Publisher> getAllPublishers() {
        return getSession().createCriteria(Publisher.class).list();
    }

    public List<Author> getAllAuthors() {
        return getSession().createCriteria(Author.class).list();
    }

    public Author getAuthor(long id) {
        return (Author) getSession().get(Author.class, id);
    }

    public void getAllBooks() {
        prepareCriterias();
        populateList();
    }

    public void getBooksByGenre(Long genreId) {

        Criterion criterion = Restrictions.eq("genre.id", genreId);

        prepareCriterias(criterion);
        populateList();
    }

    public void getBooksByLetter(Character letter) {

        Criterion criterion = Restrictions.ilike("b.name", letter.toString(), MatchMode.START);

        prepareCriterias(criterion);
        populateList();
    }

    public void getBooksByAuthor(String authorName) {

        Criterion criterion = Restrictions.ilike("author.fio", authorName, MatchMode.ANYWHERE);

        prepareCriterias(criterion);
        populateList();
    }

    public void getBooksByName(String bookName) {

        Criterion criterion = Restrictions.ilike("b.name", bookName, MatchMode.ANYWHERE);

        prepareCriterias(criterion);
        populateList();
    }

    public byte[] getContent(Long id) {
        Criteria criteria = getSession().createCriteria(Book.class);
        criteria.setProjection(Property.forName("content"));
        criteria.add(Restrictions.eq("id", id));
        return (byte[]) criteria.uniqueResult();
    }

    private void runBookListCriteria() {
        Criteria criteria = bookListCriteria.addOrder(Order.asc("b.name")).getExecutableCriteria(getSession());

        criteria.setProjection(bookProjection).setResultTransformer(Transformers.aliasToBean(Book.class));

        List<Book> list = criteria.setFirstResult(pager.getFrom()).setMaxResults(pager.getTo()).list();
        pager.setList(list);
    }

    private void runCountCriteria() {
        Criteria criteria = booksCountCriteria.getExecutableCriteria(getSession());
        Long total = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
        pager.setTotalBooksCount(total);
    }

    public void update(Book book) {
        Query query = getSession().createQuery("update Book set name = :name, "
                + " pageCount = :pageCount, "
                + " isbn = :isbn, "
                + " genre = :genre, "
                + " author = :author, "
                + " publishYear = :publishYear, "
                + " publisher = :publisher, "
                + " descr = :descr "
                + " where id = :id");
        
        query.setParameter("name", book.getName());
        query.setParameter("pageCount", book.getPageCount());
        query.setParameter("isbn", book.getIsbn());
        query.setParameter("genre", book.getGenre());
        query.setParameter("author", book.getAuthor());
        query.setParameter("publishYear", book.getPublishYear());
        query.setParameter("publisher", book.getPublisher());
        query.setParameter("descr", book.getDescr());
        query.setParameter("id", book.getId());

        int result = query.executeUpdate();
        //Session session = getSession();
        //Transaction transaction = session.getTransaction();
        // transaction.begin();

        //selectedBook.setContent(getContent(selectedBook.getId()));
        //session.update(selectedBook);

        //transaction.commit();
        //session.flush();
        //session.close();        
    }
    
    public void deleteBook(Book book) {
        Query query = getSession().createQuery("delete from Book where id = :id");
        query.setParameter("id", book.getId());
        int result = query.executeUpdate();
    }

    private void prepareCriterias(Criterion criterion) {
        bookListCriteria = DetachedCriteria.forClass(Book.class, "b");
        createAliases(bookListCriteria);
        bookListCriteria.add(criterion);

        booksCountCriteria = DetachedCriteria.forClass(Book.class, "b");
        createAliases(booksCountCriteria);
        booksCountCriteria.add(criterion);
    }

    private void prepareCriterias() {
        bookListCriteria = DetachedCriteria.forClass(Book.class, "b");
        createAliases(bookListCriteria);

        booksCountCriteria = DetachedCriteria.forClass(Book.class, "b");
        createAliases(booksCountCriteria);
    }

    private void createAliases(DetachedCriteria criteria) {
        criteria.createAlias("b.author", "author");
        criteria.createAlias("b.genre", "genre");
        criteria.createAlias("b.publisher", "publisher");
    }

    public void populateList() {
        runCountCriteria();
        runBookListCriteria();
    }
}
