/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import entity.ext.AuthorExt;
import entity.ext.GenreExt;
import entity.ext.PublisherExt;
import entity_hibernate.Book;
import entity_hibernate.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andrusha
 */
public class DataBaseTests {
    
    
    private Session session;
    private static SessionFactory sessionFactory;
    private Criteria criteria;
    private List<Book> booklist;
    private List<Book> booklist1;
    private List<AuthorExt> authorExtslist;
    private List<AuthorExt> authorExtslist1;
    private List<GenreExt> genreExtslist;
    private List<GenreExt> genreExtslist1;
    private List<PublisherExt> publisherExtslist;
    private List<PublisherExt> publisherExtslist1;
    
    public DataBaseTests() {
    }
    
    /**Ta część  kodu wykonuje się tylko jeden raz, przed pierwszym testem z tej metody. tworzę fabrykę, za pomocy której będę mógł zawsze otwierać Sesję,    potrzebuje tą fabrykę tylko jeden raz dlatego ten obiekt w @BeforeClass*/
    @BeforeClass
    public static void setUpClass() {              
       sessionFactory = HibernateUtil.getSessionFactory();
    }
    /**Ta część  kodu też wykonuje się tylko jeden raz, po ostatnim testu albo po ostatnim tegu @After jeżeli on jest*/
    @AfterClass
    public static void tearDownClass() {
    }
    /**Wykonuje się przed każdym testem, otwiera tranzakcje do BD*/
    @Before     
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();              
        
    }
    /**Wykonuje się po każdym teście, zamyka tranzakcje do BD*/
    @After
    public void tearDown() {
        session.getTransaction().commit();
    }
    
    @Test
    public void bookSelectingNotNull() {
        criteria = session.createCriteria(Book.class);
        criteria.add(Restrictions.eq("id", 12L));
        booklist = criteria.list();
        assertNotNull(booklist.get(0));
    }
    
    @Test
    public void bookEqualsTest() {
        criteria = session.createCriteria(Book.class);
        criteria.add(Restrictions.eq("id", 12L));
        booklist = criteria.list();
        booklist1 = criteria.list();
        assertEquals(booklist.get(0), booklist1.get(0));
    }
    
    @Test
    public void authorSelectingNotNull() {
        criteria = session.createCriteria(AuthorExt.class);
        criteria.add(Restrictions.eq("id", 12L));
        authorExtslist = criteria.list();
        assertNotNull(authorExtslist.get(0));
    }
    
    @Test
    public void autorEqualsTest() {
        criteria = session.createCriteria(AuthorExt.class);
        criteria.add(Restrictions.eq("id", 12L));
        authorExtslist = criteria.list();
        authorExtslist1 = criteria.list();
        assertEquals(authorExtslist.get(0), authorExtslist1.get(0));
    }

    @Test
    public void autorIsNotEqualTest() {
        criteria = session.createCriteria(AuthorExt.class);
        criteria.add(Restrictions.eq("id", 12L));
        authorExtslist = criteria.list();
        criteria = session.createCriteria(AuthorExt.class);
        criteria.add(Restrictions.eq("id", 14L));
        authorExtslist1 = criteria.list();
        assertNotEquals(authorExtslist.get(0), authorExtslist1.get(0));
    }
    
    @Test
    public void genreSelectingNotNull() {
        criteria = session.createCriteria(GenreExt.class);
        criteria.add(Restrictions.eq("id", 12L));
        genreExtslist = criteria.list();
        assertNotNull(genreExtslist.get(0));
    }
    
    @Test
    public void genreEqualsTest() {
        criteria = session.createCriteria(GenreExt.class);
        criteria.add(Restrictions.eq("id", 12L));
        genreExtslist = criteria.list();
        genreExtslist1 = criteria.list();
        assertEquals(genreExtslist.get(0), genreExtslist1.get(0));
    }

    @Test
    public void genreIsNotEqualTest() {
        criteria = session.createCriteria(GenreExt.class);
        criteria.add(Restrictions.eq("id", 12L));
        genreExtslist = criteria.list();
        criteria = session.createCriteria(GenreExt.class);
        criteria.add(Restrictions.eq("id", 14L));
        genreExtslist1 = criteria.list();
        assertNotEquals(genreExtslist.get(0), genreExtslist1.get(0));
    }
    
    @Test
    public void publisherSelectingNotNull() {
        criteria = session.createCriteria(PublisherExt.class);
        criteria.add(Restrictions.eq("id", 8L));
        publisherExtslist = criteria.list();
        assertNotNull(publisherExtslist.get(0));
    }
    
    @Test
    public void publisherEqualsTest() {
        criteria = session.createCriteria(PublisherExt.class);
        criteria.add(Restrictions.eq("id", 8L));
        publisherExtslist = criteria.list();
        publisherExtslist1 = criteria.list();
        assertEquals(publisherExtslist.get(0), publisherExtslist1.get(0));
    }

    @Test
    public void publisherIsNotEqualTest() {
        criteria = session.createCriteria(PublisherExt.class);
        criteria.add(Restrictions.eq("id", 8L));
        publisherExtslist = criteria.list();
        criteria = session.createCriteria(PublisherExt.class);
        criteria.add(Restrictions.eq("id", 9L));
        publisherExtslist1 = criteria.list();
        assertNotEquals(publisherExtslist.get(0), publisherExtslist1.get(0));
    }
    
}
