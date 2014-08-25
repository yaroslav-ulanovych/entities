package com.mahpella.entities.reading.caseclasses

import scala.reflect.ClassTag

// def begin Converter
trait Converter[From, To] {
// doc end
  val srcClass: Class[From]
  val dstClass: Class[To]
// doc begin
  def convert(x: From): Option[To]
// doc end
  override def toString = s"Converter[${srcClass.getName}, ${dstClass.getName}]"
// doc begin
}
// def end

object Converter {
  def apply[From: ClassTag, To: ClassTag](f: From => Option[To]): Converter[From, To] = {
    ConverterImpl(f)
  }
}

case class ConverterImpl[From: ClassTag, To: ClassTag](f: From => Option[To]) extends Converter[From, To] {
  val srcClass: Class[From] = implicitly[ClassTag[From]].runtimeClass.asInstanceOf[Class[From]]
  val dstClass: Class[To] = implicitly[ClassTag[To]].runtimeClass.asInstanceOf[Class[To]]

  def convert(x: From): Option[To] = f(x)
}

object BuiltInConverters {
  val BigIntToInt = ConverterImpl((f: BigInt) => if (f.isValidInt) Some(f.intValue) else None)
}