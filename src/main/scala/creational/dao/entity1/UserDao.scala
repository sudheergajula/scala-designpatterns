package creational.dao.entity1

import creational.dao.entity.User
import org.hibernate.{Session, SessionFactory}
import org.hibernate.cfg.Configuration

import scala.util.{Failure, Success, Try}

trait Dao[T] {
  def insert(obj: T): T

  def update(obj: T): T

  def delete(obj: T): Unit
}

trait Abstract[T] {

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

  def dbTrans[T](callBack: Session => T): T = {
    Try {
      val session: Session = getSession()
      session
    } match {
      case Success(session) =>
        session.beginTransaction()
        val result = callBack(session)
        session.getTransaction.commit()
        session.close()
        result
      case Failure(exception) =>
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