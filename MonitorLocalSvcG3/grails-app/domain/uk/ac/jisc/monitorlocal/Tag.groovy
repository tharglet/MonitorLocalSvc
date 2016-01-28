package uk.ac.jisc.monitorlocal;
import groovy.util.logging.Slf4j
import com.k_int.grails.tools.refdata.*

@Slf4j
class Tag extends RefdataValue {
  
    static mapping = {
    }
    
    static Tag lookupOrCreate(category_name, value) {
      return lookupOrCreate(category_name,value,null);
    }
  
    static Tag lookupOrCreate(category_name, value, sortkey) {
  
      log.debug ("Checking for ${category_name} ${value}")
      if ( value == null )
        throw new RuntimeException("Request to lookupOrCreate null value");
  
      // The category.
      def cat = RefdataCategory.findByDescription(category_name);
      def result
      
      if ( !cat ) {
        log.debug ("Creating RefdataCategory ${category_name}")
        cat = new RefdataCategory(description:category_name)
        cat.save(failOnError:true, flush:true)
        
      } else {
        log.debug ("Found RefdataCategory ${cat}")
        result = Tag.findByOwnerAndValueIlike(cat, value)
      }
      
      log.debug ("Result is ${result}")
  
      if ( !result ) {
  
        // Create and save a new refdata value.
        log.debug ("Creating RefdataValue ${value}")
        result = new Tag(owner:cat, value:(value), sortKey:(sortkey))
        result.save(failOnError:true, flush:true)
      }
  
      // return the refdata value.
      result
    }
  }

