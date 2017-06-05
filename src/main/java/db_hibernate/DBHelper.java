package db_hibernate;

import entity.ext.AuthorExt;
import entity.ext.BookExt;
import entity.ext.GenreExt;
import entity.ext.PublisherExt;
import entity_hibernate.HibernateUtil;
import java.util.List;
import view.PageOfBooks;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
    private PageOfBooks pageOfBooks;
    private ProjectionList bookProjection;

    public DBHelper(PageOfBooks pageOfBooks) {  //tworzenie objektu DAO dla pracy z BD, projekcji zrobione zeby nie pobierac kazdy raz czesc pdf
        this.pageOfBooks = pageOfBooks;
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

    private Session getSession() {  //otwiera connection do BD
        return sessionFactory.getCurrentSession();
    }

    public List<GenreExt> getAllGenres() {      //pobiera wszystkie ksiazki
        return getSession().createCriteria(GenreExt.class).list();
    }

    public List<PublisherExt> getAllPublishers() { //pobiera wszystkie wydawnictwa
        return getSession().createCriteria(PublisherExt.class).list();
    }

    public List<AuthorExt> getAllAuthors() {    //pobiera wszystkich autorow
        return getSession().createCriteria(AuthorExt.class).list();
    }

    public AuthorExt getAuthor(long id) {   //pobiera autora po id
        return (AuthorExt) getSession().get(AuthorExt.class, id);
    }

    public void getAllBooks() { //pobiera wszystkie ksiazki
        prepareCriterias(); 
        populateList();     
    }

    public void getBooksByGenre(Long genreId) { //pobiera ksiazki po id gatunka

        Criterion criterion = Restrictions.eq("genre.id", genreId);

        prepareCriterias(criterion);
        populateList();
    }

    public void getBooksByLetter(Character letter) {    //pobiera ksiazki po pierwszej literie w nazwie

        Criterion criterion = Restrictions.ilike("b.name", letter.toString(), MatchMode.START);

        prepareCriterias(criterion);
        populateList();
    }

    public void getBooksByAuthor(String authorName) {   //pobiera ksiazki po imionach autora

        Criterion criterion = Restrictions.ilike("author.allNames", authorName, MatchMode.ANYWHERE);

        prepareCriterias(criterion);
        populateList();
    }

    public void getBooksByName(String bookName) { //pobiera ksiazki po nazwie

        Criterion criterion = Restrictions.ilike("b.name", bookName, MatchMode.ANYWHERE);

        prepareCriterias(criterion);
        populateList();
    }

    public void SortAllBooks(Character kind) { //sortuje ksiazki po nowosci
        prepareCriterias();
        if (kind.equals('F')) {
            runCountCriteria();
            Criteria criteria = bookListCriteria.addOrder(Order.desc("b.id")).getExecutableCriteria(getSession());
            criteria.setProjection(bookProjection).setResultTransformer(Transformers.aliasToBean(BookExt.class));
            List<BookExt> list = criteria.setFirstResult(pageOfBooks.getStart()).setMaxResults(pageOfBooks.getAmountOnOnePage()).list();
            pageOfBooks.setListOfBookExts(list);
        }
    }
    
    public boolean isIsbnExist(String isbn, Long id) {  //sprawdza czy istnieje taki isbn
        
        Criteria criteria = getSession().createCriteria(BookExt.class,"b");
        criteria.createAlias("b.author", "author");
        criteria.createAlias("b.genre", "genre");
        criteria.createAlias("b.publisher", "publisher");
        criteria.add(Restrictions.ilike("b.isbn", isbn, MatchMode.EXACT));
        
        if (id!=null){
            criteria.add(Restrictions.not(Restrictions.eq("b.id", id)));
        }

        Long total = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();

        return total>=1;
        
    }

    public byte[] getContent(Long id) {     //pobiera pdf
        Criteria criteria = getSession().createCriteria(BookExt.class);
        criteria.setProjection(Property.forName("content"));
        criteria.add(Restrictions.eq("id", id));
        return (byte[]) criteria.uniqueResult();
    }

    private void runBookListCriteria() {        //pobira ksiazki na zapyt
        Criteria criteria = bookListCriteria.addOrder(Order.asc("b.name")).getExecutableCriteria(getSession());
        criteria.setProjection(bookProjection).setResultTransformer(Transformers.aliasToBean(BookExt.class));
        List<BookExt> list = criteria.setFirstResult(pageOfBooks.getStart()).setMaxResults(pageOfBooks.getAmountOnOnePage()).list();
        pageOfBooks.setListOfBookExts(list);
    }

    private void runCountCriteria() {   //wylicza islosc ksiazek na zapyt
        Criteria criteria = booksCountCriteria.getExecutableCriteria(getSession());
        Long total = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
        pageOfBooks.setTotalBooksCount(total);
    }
    
    public void add(BookExt book) {     //dodaje ksiazke
        getSession().save(book);
        book.setContentEdited(false);
        book.setImageEdited(false);
    }

    public void update(BookExt book) {    //odnawia jakas ksiazke
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("update BookExt ");
        queryBuilder.append("set name = :name, ");
        queryBuilder.append("pageCount = :pageCount, ");
        queryBuilder.append("isbn = :isbn, ");
        queryBuilder.append("genre = :genre, ");
        queryBuilder.append("author = :author, ");
        queryBuilder.append("publishYear = :publishYear, ");
        queryBuilder.append("publisher = :publisher, ");
        if (book.isImageEdited()) {
            queryBuilder.append("image = :image, ");
        }
        if (book.isContentEdited()) {
            queryBuilder.append("content = :content, ");
        }
        queryBuilder.append("descr = :descr ");
        queryBuilder.append("where id = :id");

        Query query = getSession().createQuery(queryBuilder.toString());

        query.setParameter("name", book.getName());
        query.setParameter("pageCount", book.getPageCount());
        query.setParameter("isbn", book.getIsbn());
        query.setParameter("genre", book.getGenre());
        query.setParameter("author", book.getAuthor());
        query.setParameter("publishYear", book.getPublishYear());
        query.setParameter("publisher", book.getPublisher());
        query.setParameter("descr", book.getDescr());
        query.setParameter("id", book.getId());
        if (book.isImageEdited()) {
            query.setParameter("image", book.getImage());
        }
        if (book.isContentEdited()) {
            query.setParameter("content", book.getContent());
        }
        int result = query.executeUpdate();
        book.setContentEdited(false);
        book.setImageEdited(false);

        //Session session = getSession();
        //Transaction transaction = session.getTransaction();
        //transaction.begin();
        //selectedBook.setContent(getContent(selectedBook.getId()));
        //session.update(selectedBook);
        //transaction.commit();
        //session.flush();
        //session.close();        
    }

    public void deleteBook(BookExt book) {      //usuwa z BD ksiazke
        Query query = getSession().createQuery("delete from BookExt where id = :id");
        query.setParameter("id", book.getId());
        int result = query.executeUpdate();
    }

    private void prepareCriterias(Criterion criterion) {    //tworzy rodzaj kryterij do których nie jest potrzebne otwieranie sesji
        bookListCriteria = DetachedCriteria.forClass(BookExt.class, "b");
        createAliases(bookListCriteria);
        bookListCriteria.add(criterion);

        booksCountCriteria = DetachedCriteria.forClass(BookExt.class, "b");
        createAliases(booksCountCriteria);
        booksCountCriteria.add(criterion);
    }

    private void prepareCriterias() {   //tworzy aliasy
        bookListCriteria = DetachedCriteria.forClass(BookExt.class, "b");
        createAliases(bookListCriteria);

        booksCountCriteria = DetachedCriteria.forClass(BookExt.class, "b");
        createAliases(booksCountCriteria);
    }

    private void createAliases(DetachedCriteria criteria) {
        criteria.createAlias("b.author", "author");
        criteria.createAlias("b.genre", "genre");
        criteria.createAlias("b.publisher", "publisher");
    }

    public void populateList() {    //wylicza ilosc ksiazek na ten query i pobira wszytkie ksiazki
        runCountCriteria();
        runBookListCriteria();
    }
}
