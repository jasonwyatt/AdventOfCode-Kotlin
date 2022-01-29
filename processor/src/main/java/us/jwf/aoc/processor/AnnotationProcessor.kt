package us.jwf.aoc.processor

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.element.TypeElement

class AnnotationProcessor : AbstractProcessor() {
  override fun getSupportedAnnotationTypes(): MutableSet<String> {
    return mutableSetOf(Day::class.java.name)
  }


  override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
    TODO("Not yet implemented")
  }
}