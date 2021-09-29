package com.designpatterns.creational.caching.store.db

import com.designpatterns.creational.caching.entity.UserAccount
import org.hibernate.cfg.Configuration
import org.hibernate.{Session, SessionFactory, Transaction}

import scala.util.{Failure, Success, Try}


trait Dao[T] {
  def insert(obj: T): T

  def update(obj: T): T

  def delete(obj: T): T
}

protected abstract class GenericDao[T] extends Dao[T] {

  var sessionFactory = buildSessionFactory

  var session: Session = _
  var transaction: Transaction = _

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
      session = getSessionFactory.openSession()
      session
    } match {
      case Success(session) => {
        session.beginTransaction()
        session.save(obj)
        onSuccess(session)
        obj
      }
      case Failure(exception) =>
        onFailure(session)
        throw exception
    }
  }

  private def onFailure(session: Session): Unit = {
    if (session != null && session.isOpen) {
      session.getTransaction.rollback()
      session.flush()
      session.close()
    }
  }

  override def update(obj: T): T = {
    Try {
      session = getSessionFactory.openSession()
      session
    } match {
      case Success(session) => {
        session.merge(obj)
        session.beginTransaction()
        onSuccess(session)
        obj
      }
      case Failure(exception) =>
        onFailure(session)
        throw exception
    }
  }

  def onSuccess(session: Session): Unit = {
    session.flush()
    session.getTransaction.commit()
    session.close()
  }


  override def delete(obj: T): T = {
    Try {
      session = getSessionFactory.openSession()
      session
    } match {
      case Success(session) =>
        session.delete(obj)
        session.beginTransaction()
        onSuccess(session)
        obj
      case Failure(exception) =>
        onFailure(session)
        throw exception
    }
  }
}

class UserDao extends GenericDao[UserAccount] {

  def addUser(user: UserAccount): UserAccount = {
    insert(user)
  }

  def removeUser(user: UserAccount): UserAccount = {
    delete(user)
  }

  def getUserByName(firstName: String, lastName: String): UserAccount = {
    val query = getSession.createQuery(s"from User where first_name =:firstName and last_name =:lastName",
      classOf[UserAccount])
    query.setParameter("firstName", firstName)
    query.setParameter("lastName", lastName)
    query.getSingleResult
  }

  def getUserById(id: Int): UserAccount = {
    val result = getSession.get(classOf[UserAccount], id)
    result
  }

  def removeUserByName(firstName: String, lastName: String): Unit = {
    val user = getUserByName(firstName, lastName)
    delete(user)
  }

  def updateUser(user: UserAccount): UserAccount = {
    update(user)
  }

}