package creational.dao.entity2

import creational.dao.entity
import creational.dao.entity.User
import org.hibernate.cfg.Configuration
import org.hibernate.{Session, SessionFactory, Transaction}

trait Dao[E] {

  def insert(obj: E): E

  def delete(obj: E): Unit

  def update(obj: E): E
}

abstract class AbstractDao[E] extends Dao[E] {

  private var session: Session = _
  private var transaction: Transaction = _
  private var sessionFactory: SessionFactory = buildSessionFactory()

  def getSession: Session = sessionFactory.openSession()


  def buildSessionFactory(): SessionFactory = {
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

  def dbTrx(callback: Session => E): E = {
    try {
      session = sessionFactory.openSession()
      transaction = session.beginTransaction()
      val result = callback(session)
      transaction.commit()
      result
    } catch {
      case e: Exception =>
        transaction.rollback()
        throw e
    } finally {
      if (session != null && session.isOpen)
        session.close()
    }

  }

  def dbSession(callback: (Session) => E): E = {
    try {
      session = sessionFactory.openSession()
      val result = callback(session)
      result
    } catch {
      case e: Exception =>
        throw e
    } finally {
      if (session != null && session.isOpen)
        session.close()
    }

  }
}

class UserDao extends AbstractDao[User] with Dao[User] {
  override def insert(user: User): User = {
    dbTrx(session => {
      session.save(user)
      user
    })
  }

  override def delete(user: User): Unit = {
    dbTrx(session => {
      session.delete(user)
      user
    })
  }

  override def update(user: User): User = {
    dbTrx(session => {
      session.merge(user)
      user
    })
  }

  def getUserById(id: Int): entity.User ={
    dbSession(session => {
      val user = session.get(classOf[entity.User], id)
      user
    })
  }
}
