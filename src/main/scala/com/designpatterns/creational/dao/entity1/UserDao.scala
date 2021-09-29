package com.designpatterns.creational.dao.entity1

import com.designpatterns.creational.dao.entity.User
import org.hibernate.cfg.Configuration
import org.hibernate.{Session, SessionFactory, Transaction}

import scala.util.{Failure, Success, Try}

trait Dao[T] {
  def insert(obj: T): T

  def update(obj: T): T

  def delete(obj: T): Unit
}

trait Abstract[T] {
  private var session: Session = _
  private var transaction: Transaction = _
  var sessionFactory: SessionFactory = buildSession()

  def getSession() = sessionFactory.openSession()

  def buildSession(): SessionFactory = {
    try {
      val dbConfig: Configuration = new Configuration().configure("hibernate.cfg.xml")
      sessionFactory = dbConfig.configure.buildSessionFactory
      sessionFactory
    } catch {
      case ex: Throwable =>
        System.err.println("Initial SessionFactory creation failed." + ex);
        throw new ExceptionInInitializerError(ex);
    }
  }

  def destroySession(): Unit = {
    if (session.isOpen) {
      session.flush()
      session.close()
    }
  }

  def dbTrans[T](callBack: Session => T): T = {
    Try {
      session = getSession()
      session
    } match {
      case Success(session) =>
        session.beginTransaction()
        val result = callBack(session)
        session.getTransaction.commit()
        destroySession()
        result
      case Failure(exception) =>
        if (transaction != null) {
          transaction.rollback()
          destroySession()
        }
        throw exception
    }
  }
}


class UserDao extends Abstract[User] {

  def getUserByName(firstName: String, lastName: String): User = {
    dbTrans(session => {
      val query = session.createQuery(s"from User where first_name =:firstName and last_name =:lastName",
        classOf[User])
      query.setParameter("firstName", firstName)
      query.setParameter("lastName", lastName)
      query.getSingleResult
    })
  }

  def createUser(fName: String,
                 lName: String,
                 age: Int,
                 gender: String): User = {

    dbTrans(session => {
      val user = new User
      user
        .setFirstName(fName)
        .setLastName(lName)
        .setAge(age)
        .setGender(gender)
      val result = session.save(user)
      user
    }
    )
  }

}