package com.designpatterns.persistence.dao.entity

import org.hibernate.cfg.Configuration
import org.hibernate.{Session, SessionFactory}

import scala.util.{Failure, Success, Try}


trait Dao[T] {
  def insert(obj: T): T

  def update(obj: T): T

  def delete(obj: T): T
}

protected abstract class GenericDao[T] extends Dao[T] {

  var sessionFactory = buildSessionFactory

  protected def getSessionFactory: SessionFactory = sessionFactory

  private def buildSessionFactory: SessionFactory = {
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

  protected def getSession: Session = {
    getSessionFactory.openSession()
  }

  override def insert(obj: T): T = {
    Try {
      val currentSession = getSessionFactory.openSession()
      currentSession
    } match {
      case Success(session) => {
        session.save(obj)
        onSuccess(session)
        obj
      }
      case Failure(exception) =>
        throw exception
    }
  }

  override def update(obj: T): T = {
    Try {
      val currentSession = getSessionFactory.openSession()
      currentSession
    } match {
      case Success(session) => {
        session.merge(obj)
        val persistedObj = session.beginTransaction()
        onSuccess(session)
        obj
      }
      case Failure(exception) =>
        throw exception
    }
  }

  def onSuccess(session: Session) = {
    session.flush()
    session.getTransaction.commit()
    session.close()
  }


  override def delete(obj: T): T = {
    Try {
      val currentSession = getSessionFactory.openSession()
      currentSession
    } match {
      case Success(session) => {
        session.delete(obj)
        session.beginTransaction()
        onSuccess(session)
        obj
      }
      case Failure(exception) =>
        throw exception
    }
  }
}

class UserDao extends GenericDao[User] {

  def addUser(fName: String,
              lName: String,
              age: Int,
              gender: String): User = {
    val user = new User
    user
      .setFirstName(fName)
      .setLastName(lName)
      .setAge(age)
      .setGender(gender)
    insert(user)
  }

  def removeUser(user: User): User = {
    delete(user)
  }

  def getUserByName(firstName: String, lastName: String): User = {
    val query = getSession.createQuery(s"from User where first_name =:firstName and last_name =:lastName",
      classOf[User])
    query.setParameter("firstName", firstName)
    query.setParameter("lastName", lastName)
    query.getSingleResult
  }

  def getUserById(id: Int): User = {
    val result = getSession.get(classOf[User], id)
    result
  }

  def removeUserByName(firstName: String, lastName: String): Unit = {
    val user = getUserByName(firstName, lastName)
    delete(user)
  }

  def updateUser(user: User): User = {
    update(user)
  }

}