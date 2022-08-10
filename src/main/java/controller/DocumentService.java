package controller;

import model.DriverLicense;
import model.Passport;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.Scanner;

public class DocumentService {
    private final Configuration config = new Configuration().configure();
    private final SessionFactory sessionFactory = config.buildSessionFactory();

    public void printAllPassports(){
    Session session = sessionFactory.openSession();
    Query<Passport> query = session.createNativeQuery("select * from passports",Passport.class);
    query.getResultList().forEach(System.out::println);
  }

    public void addDriveLicenseToUser(int series,int number){
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        Query<Passport> query = session.createQuery("select p from Passport p where p.series = :series and p.number = :number", Passport.class);
        query.setParameter("series",series);
        query.setParameter("number",number);
        Passport passport = query.getSingleResult();
        Integer id = passport.getUser().getId();
        User user = session.get(User.class, id);
        DriverLicense driverLicense = new DriverLicense((int) (Math.random() * 999999) + 1);
        driverLicense.setUser(user);
        try {
            session.persist(driverLicense);
            session.getTransaction().commit();
        }
        catch (Exception e){
            addDriveLicenseToUser(series,number);
        }
    }

    public void addPassport(String firstName,String lastName,int series,int number){
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        Passport passport = new Passport(series, number);
        User user = new User(firstName,lastName,14);
        passport.setUser(user);
        try {
            session.persist(passport);
            session.getTransaction().commit();
        }
        catch (Exception e){
            System.err.println("Уже существует паспорт с таким номером\nВведите другой номер:");
            Scanner scn = new Scanner(System.in);
            passport = new Passport(series,scn.nextInt());
            passport.setUser(user);
            session.persist(passport);
            session.getTransaction().commit();
        }
    }

    public void printAllDriveLicense(){
        Session session = sessionFactory.openSession();
        Query<DriverLicense> query = session.createNativeQuery("select * from DriverLicense",DriverLicense.class);
        query.getResultList().forEach(System.out::println);
    }

    public void printFirstNameAndLastNameBySeriesAndNumber(int series,int number){
        Session session = sessionFactory.openSession();
        Query<Passport> query = session.createQuery("select p from Passport p where p.series = :series and p.number = :number", Passport.class);
        query.setParameter("series",series);
        query.setParameter("number",number);
        Passport passport = query.getSingleResult();
        System.out.println(passport.getUser().getFirstName() + " " + passport.getUser().getLastName());
    }

}



