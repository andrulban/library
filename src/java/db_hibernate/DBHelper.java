/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_hibernate;

import entity_hibernate.Author;
import entity_hibernate.Book;
import entity_hibernate.Genre;
import entity_hibernate.HibernateUtil;
import entity_hibernate.Publisher;
import java.util.List;
import jbeans.Pager;
import org.hibernate.Criteria;
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
    private static DBHelper dataHelper;
    private DetachedCriteria bookListCriteria;
    private DetachedCriteria booksCountCriteria;
    private Pager currentPager;
    private ProjectionList bookProjection;

    private DBHelper() {
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
    }

    public static DBHelper getInstance() {
        if (dataHelper == null) {
            dataHelper = new DBHelper();
        }
        return dataHelper;
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

    public void getAllBooks(Pager pager) {
        currentPager = pager;

        createBooksCountCriteria();
        runCountCriteria();

        createBookListCriteria();
        runBookListCriteria();

    }

    public void getBooksByGenre(Long genreId, Pager pager) {
        currentPager = pager;

        Criterion criterion = Restrictions.eq("genre.id", genreId);

        createBooksCountCriteria(criterion);
        runCountCriteria();

        createBookListCriteria(criterion);
        runBookListCriteria();
    }

    public void getBooksByLetter(Character letter, Pager pager) {
        currentPager = pager;

        Criterion criterion = Restrictions.ilike("b.name", letter.toString(), MatchMode.START);

        createBooksCountCriteria(criterion);
        runCountCriteria();

        createBookListCriteria(criterion);
        runBookListCriteria();
    }

    public void getBooksByAuthor(String authorName, Pager pager) {
        currentPager = pager;

        Criterion criterion = Restrictions.ilike("author.fio", authorName, MatchMode.ANYWHERE);

        createBooksCountCriteria(criterion);
        runCountCriteria();

        createBookListCriteria(criterion);
        runBookListCriteria();
    }

    public void getBooksByName(String bookName, Pager pager) {
        currentPager = pager;

        Criterion criterion = Restrictions.ilike("b.name", bookName, MatchMode.ANYWHERE);

        createBooksCountCriteria(criterion);
        runCountCriteria();

        createBookListCriteria(criterion);
        runBookListCriteria();
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

        List<Book> list = criteria.setFirstResult(currentPager.getFrom()).setMaxResults(currentPager.getTo()).list();
        currentPager.setList(list);
    }

    private void runCountCriteria() {
        Criteria criteria = booksCountCriteria.getExecutableCriteria(getSession());
        Long total = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);
    }

    public void update() {
        Session session = getSession();
        //Transaction transaction = session.getTransaction();
       // transaction.begin();
        for (Object object : currentPager.getList()) {
            Book book = (Book) object;
            if (book.isIsEditing()) {
                book.setContent(getContent(book.getId()));
                session.update(book);
            }

        }
        //transaction.commit();
        //session.flush();
        //session.close();

    }

    private void createBooksCountCriteria(Criterion criterion) {
        booksCountCriteria = DetachedCriteria.forClass(Book.class, "b");
        booksCountCriteria.add(criterion);
    }

    private void createBooksCountCriteria() {
        booksCountCriteria = DetachedCriteria.forClass(Book.class, "b");
    }

    private void createBookListCriteria(Criterion criterion) {
        bookListCriteria = DetachedCriteria.forClass(Book.class, "b");
        bookListCriteria.add(criterion);
        createAliases();

    }

    private void createBookListCriteria() {
        bookListCriteria = DetachedCriteria.forClass(Book.class, "b");
        createAliases();
    }

    private void createAliases() {
        bookListCriteria.createAlias("b.author", "author");
        bookListCriteria.createAlias("b.genre", "genre");
        bookListCriteria.createAlias("b.publisher", "publisher");
    }

    public void refreshList() {
        runCountCriteria();
        runBookListCriteria();
    }
}