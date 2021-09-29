package com.designpatterns.creational.dao.entity

import javax.persistence.{GenerationType, _}

@Table(name = "user")
@Entity
class User extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(insertable = false, name = "id", nullable = false)
  var id: Int = _

  @Column(name = "fisrt_name", nullable = false)
  var first_name: String = _

  @Column(name = "last_name", nullable = false)
  var last_name: String = _

  @Column(name = "age", nullable = false)
  var age: Int = _

  @Column(name = "gender", nullable = false)
  var gender: String = _

  def setFirstName(name: String): User = {
    this.first_name = name
    this
  }

  def setLastName(name: String): User = {
    this.last_name = name
    this
  }

  def setAge(age: Int): User = {
    this.age = age
    this
  }

  def setGender(gender: String): User = {
    this.gender = gender
    this
  }

  override def toString: String = s"User(id: ${id}, firstName: $first_name, lastName: $last_name, age:$age, gender: $gender)"

}
