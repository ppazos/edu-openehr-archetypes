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

   static Archetype archetype

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
      archetype = parse(pathToADL)

      Interval.metaClass.toString = {
         "Interval\t" + (delegate.lower != null ? delegate.lower.toString() : '*') +'..'+ (delegate.upper != null ? delegate.upper.toString() : '*')
      }

      // Services.Traverse
      Services.Traverse traverse = new Services.Traverse()
      traverse.run(archetype)
   }

   static void render(Path pathToADL, String archetypePath)
   {
      archetype = parse(pathToADL)

      ArchetypeConstraint c = archetype.node(archetypePath)

      if (!c)
      {
         throw new Exception("There is no constraint at the given path ${archetypePath} on ${archetype.archetypeId.value}")
      }

      Services.ConstraintRender render = new Services.ConstraintRender()
      render.render(c)
   }

   // common util method
   static String nodeName(CObject c)
   {
      if (!c.nodeId) return ''

      def term = archetype.ontology.termDefinition(archetype.originalLanguage.codeString, c.nodeId)

      if (!term) return ''

      ' ('+ term?.text +')'
   }

   static class Traverse {

      int level = 0
      Archetype archetype

      def run(Archetype archetype)
      {
         this.archetype = archetype
         run(archetype.definition)
      }

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

         // NOTE: this will detect any missing methods specific for the constraint type
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
            println indent(it == c.list.last()) + c.rmTypeName +"\t"+ it.units +" "+ (it.magnitude ? (it.magnitude.lower +".."+ it.magnitude.upper) : ('*..*'))
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
         println indent(true) + 'CREF ('+ c.reference +'): '+ c.path()
      }

      def run(ArchetypeInternalRef c)
      {
         println indent(true) + 'IREF ('+ c.targetPath +'): '+ c.path()
      }

      def run(ArchetypeSlot c)
      {
         println indent(false) + "SLOT: "+ c.path()
         c.includes.each { assertion ->
            println indent(assertion == c.includes.last()) + assertion.stringExpression
         }
      }


      def run(CComplexObject o)
      {
         //println o.getClass().getSimpleName() +"\t"+ o.rmTypeName.padLeft(15) +"\t"+ o.path()
         println indent() + o.rmTypeName + nodeName(o) + ': '+ o.path()
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
         a.children.each {
            level++
            run(it)
            level --
         }
      }
   }

   // Use to render single constraint nodes
   static class ConstraintRender {

      def render(CObject o)
      {
         //println o.getClass().getSimpleName() +"\t"+ o.rmTypeName.padLeft(15) +"\t"+ o.path()

         println o.rmTypeName +': '+ o.path()

         // NOTE: this will detect any missing methods specific for the constraint type
         println o.getClass()

         /*
         org.openehr.am.openehrprofile.datatypes.text.CCodePhrase
         org.openehr.am.archetype.constraintmodel.ArchetypeSlot
         org.openehr.am.archetype.constraintmodel.ArchetypeInternalRef
         org.openehr.am.archetype.constraintmodel.CPrimitiveObject
         org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantity
         */
      }

      def render(CPrimitiveObject c)
      {
         render(c.item)
      }

      def render(CString c)
      {
         if (c.pattern) println c.pattern
         else println c.list
      }

      def render(CInteger c)
      {
         if (c.interval) println c.interval.toString()
         else println c.list
      }

      def render(CDateTime c)
      {
         if (c.interval) println c.interval.toString()
         else println c.pattern
      }

      def render(CDate c)
      {
         if (c.interval) println c.interval.toString()
         else println c.pattern
      }

      def render(CTime c)
      {
         if (c.interval) println c.interval.toString()
         else println c.pattern
      }

      def render(CReal c)
      {
         if (c.interval) println c.interval.toString()
         else println c.list
      }

      def render(CDuration c)
      {
         if (c.interval) println c.interval.toString()
         else println c.value
      }

      def render(CBoolean c)
      {
         println "trueValid: "+ c.trueValid +" falseValid: "+ c.falseValid
      }

      def render(CDvOrdinal c)
      {
         c.list.each {
            println c.rmTypeName +"\t"+ it.value +" "+ it.symbol.terminologyId.value +'::'+ it.symbol.codeString
         }
      }

      def render(CDvQuantity c)
      {
         println c.rmTypeName +' <'+ c.class.simpleName +'>'
         c.list.each {
            println it.units +' '+ (it.magnitude ? (it.magnitude.lower +".."+ it.magnitude.upper) : ('*..*'))
         }
      }

      def render(CCodePhrase c)
      {
         println c.rmTypeName +' <'+ c.class.simpleName +'>'
         c.codeList.each {
            println c.terminologyId.value +"::"+ it
         }
      }

      def render(ConstraintRef c)
      {
         println c.rmTypeName +' '+ c.reference +' <'+ c.class.simpleName +'>'
      }

      def render(ArchetypeInternalRef c)
      {
         println c.rmTypeName +' '+ c.targetPath +' <'+ c.class.simpleName +'>'
      }

      def render(ArchetypeSlot c)
      {
         println c.rmTypeName +' <'+ c.class.simpleName +'>'
         c.includes.each { assertion ->
            println assertion.stringExpression
         }
      }

      def render(CComplexObject o)
      {
         //println o.getClass().getSimpleName() +"\t"+ o.rmTypeName.padLeft(15) +"\t"+ o.path()
         println o.rmTypeName + nodeName(o) + ' <'+ o.class.simpleName +'>'
         // o.attributes.each {
         //    render(it)
         // }
      }
   }
}