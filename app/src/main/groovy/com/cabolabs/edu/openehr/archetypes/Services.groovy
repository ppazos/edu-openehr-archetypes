package com.cabolabs.edu.openehr.archetypes

import java.nio.file.Path
import se.acode.openehr.parser.*
import org.openehr.am.archetype.*
import java.nio.file.Files
import org.openehr.am.archetype.constraintmodel.*
import org.openehr.am.archetype.constraintmodel.primitive.*
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantity
import org.openehr.am.openehrprofile.datatypes.quantity.CDvOrdinal
import org.openehr.am.openehrprofile.datatypes.text.CCodePhrase
import org.openehr.rm.support.basic.Interval

class Services {

   static String v = '├'
   static String h = '─'
   static String p = '│'
   static String l = '└'

   static Archetype parse(Path pathToADL)
   {
      if (!Files.exists(pathToADL))
      {
         println "Service.parse() ERROR: file $pathToADL doesn't exist"
         return
      }

      BufferedReader br = Files.newBufferedReader(pathToADL)
      ADLParser parser = null

      try
      {
         parser = new ADLParser(br)
      }
      catch (IOException e)
      {
         println "Service.parse() ERROR: "+ e.message
         return
      }

      Archetype archetype = null
      try
      {
         archetype = parser.archetype()
      }
      catch (Exception e)
      {
         println "Service.parse() ERROR: "+ e.message
         return
      }

      return archetype
   }

   static void traverse(Path pathToADL)
   {
      Archetype archetype = parse(pathToADL)

      Interval.metaClass.toString = {
         "Interval\t" + (delegate.lower != null ? delegate.lower.toString() : '*') +'..'+ (delegate.upper != null ? delegate.upper.toString() : '*')
      }

      Services.Traverse traverse = new Services.Traverse()
      traverse.run(archetype.definition)
   }

   static class Traverse {

      int level = 0

      def indent(boolean last = false)
      {
         //"  ".multiply(level) + v +" "
         if (last)
           (p +' ').multiply(level) + l + ' '
         else
           (p +' ').multiply(level) + v + ' '
      }

      def run(CObject o)
      {
         //println o.getClass().getSimpleName() +"\t"+ o.rmTypeName.padLeft(15) +"\t"+ o.path()

         println indent() + o.rmTypeName +': '+ o.path()

         println o.getClass()
         /*
         org.openehr.am.openehrprofile.datatypes.text.CCodePhrase
         org.openehr.am.archetype.constraintmodel.ArchetypeSlot
         org.openehr.am.archetype.constraintmodel.ArchetypeInternalRef
         org.openehr.am.archetype.constraintmodel.CPrimitiveObject
         org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantity
         */
      }

      def run(CPrimitiveObject c)
      {
         run(c.item)
      }

      def run(CString c)
      {
         if (c.pattern) println indent(true) + c.pattern
         else println indent(true) + c.list
      }

      def run(CInteger c)
      {
         if (c.interval) println indent(true) + c.interval.toString()
         else println indent(true) + c.list
      }

      def run(CDateTime c)
      {
         if (c.interval) println indent(true) + c.interval.toString()
         else println indent(true) + c.pattern
      }

      def run(CDate c)
      {
         if (c.interval) println indent(true) + c.interval.toString()
         else println indent(true) + c.pattern
      }

      def run(CTime c)
      {
         if (c.interval) println indent(true) + c.interval.toString()
         else println indent(true) + c.pattern
      }

      def run(CReal c)
      {
         if (c.interval) println indent(true) + c.interval.toString()
         else println indent(true) + c.list
      }

      def run(CDuration c)
      {
         if (c.interval) println indent(true) + c.interval.toString()
         else println indent(true) + c.value
      }

      def run(CBoolean c)
      {
         println indent(true) + "trueValid: "+ c.trueValid +" falseValid: "+ c.falseValid
      }

      def run(CDvOrdinal c)
      {
         c.list.each {
            println indent(it == c.list.last()) + c.rmTypeName +"\t"+ it.value +" "+ it.symbol.terminologyId.value +'::'+ it.symbol.codeString
         }
      }

      def run(CDvQuantity c)
      {
         c.list.each {
            println indent(it == c.list.last()) + c.rmTypeName +"\t"+ it.units +" "+ it.magnitude.lower +".."+ it.magnitude.upper
         }
      }

      def run(CCodePhrase c)
      {
         c.codeList.each {
            println indent(it == c.codeList.last()) + c.rmTypeName +"\t"+ c.terminologyId.value +"::"+ it
         }
      }

      def run(ConstraintRef c)
      {
         println indent(true) + c.reference
      }

      def run(CComplexObject o)
      {
         //println o.getClass().getSimpleName() +"\t"+ o.rmTypeName.padLeft(15) +"\t"+ o.path()
         println indent() + o.rmTypeName +': '+ o.path()
         o.attributes.each {
            level ++
            run(it)
            level --
         }
      }

      def run(CAttribute a)
      {
         //println a.getClass().getSimpleName() +"\t"+ a.rmAttributeName.padLeft(15) +"\t"+ a.path()
         println indent() + a.rmAttributeName +": "+ a.path()
         a.children.each{
            level++
            run(it)
            level --
         }
      }
   }

}