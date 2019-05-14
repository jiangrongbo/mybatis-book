/**
 *    Copyright 2012-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.scripting.velocity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.ParameterExpression;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;

public class ParameterMappingSourceParser {

  private static final String VALID_PROPERTIES = "javaType,jdbcType,mode,numericScale,resultMap,typeHandler,jdbcTypeName";

  private final String sql;

  private final ParameterMapping[] parameterMappingSources;

  public ParameterMappingSourceParser(Configuration configuration, String script, Class<?> parameterType) {
    ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration, parameterType);
    GenericTokenParser parser = new GenericTokenParser("@{", "}", handler);
    this.sql = parser.parse(script);
    this.parameterMappingSources = handler.getParameterMappingSources();
  }

  public ParameterMapping[] getParameterMappingSources() {
    return this.parameterMappingSources;
  }

  public String getSql() {
    return this.sql;
  }

  private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {

    private final List<ParameterMapping> parameterMappings = new ArrayList<>();
    private final Class<?> parameterType;

    public ParameterMappingTokenHandler(Configuration newConfiguration, Class<?> newParameterType) {
      super(newConfiguration);
      this.parameterType = newParameterType;
    }

    public ParameterMapping[] getParameterMappingSources() {
      return this.parameterMappings.toArray(new ParameterMapping[this.parameterMappings.size()]);
    }

    @Override
    public String handleToken(String content) {
      int index = this.parameterMappings.size();
      ParameterMapping pm = buildParameterMapping(content);
      this.parameterMappings.add(pm);
      return new StringBuilder().append('$').append(SQLScriptSource.MAPPING_COLLECTOR_KEY).append(".g(").append(index)
          .append(")").toString();
    }

    private ParameterMapping buildParameterMapping(String content) {
      Map<String, String> propertiesMap = parseParameterMapping(content);
      String property = propertiesMap.get("property");
      String jdbcType = propertiesMap.get("jdbcType");
      Class<?> propertyType;
      if (this.typeHandlerRegistry.hasTypeHandler(this.parameterType)) {
        propertyType = this.parameterType;
      } else if (JdbcType.CURSOR.name().equals(jdbcType)) {
        propertyType = java.sql.ResultSet.class;
      } else if (property != null) {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaClass metaClass = MetaClass.forClass(this.parameterType, reflectorFactory);
        if (metaClass.hasGetter(property)) {
          propertyType = metaClass.getGetterType(property);
        } else {
          propertyType = Object.class;
        }
      } else {
        propertyType = Object.class;
      }
      ParameterMapping.Builder builder = new ParameterMapping.Builder(this.configuration, property, propertyType);
      if (jdbcType != null) {
        builder.jdbcType(resolveJdbcType(jdbcType));
      }
      Class<?> javaType = null;
      String typeHandlerAlias = null;
      for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
        String name = entry.getKey();
        String value = entry.getValue();
        if ("javaType".equals(name)) {
          javaType = resolveClass(value);
          builder.javaType(javaType);
        } else if ("jdbcType".equals(name)) {
          builder.jdbcType(resolveJdbcType(value));
        } else if ("mode".equals(name)) {
          builder.mode(resolveParameterMode(value));
        } else if ("numericScale".equals(name)) {
          builder.numericScale(Integer.valueOf(value));
        } else if ("resultMap".equals(name)) {
          builder.resultMapId(value);
        } else if ("typeHandler".equals(name)) {
          typeHandlerAlias = value;
        } else if ("jdbcTypeName".equals(name)) {
          builder.jdbcTypeName(value);
        } else if ("property".equals(name)) {
          // Do Nothing
        } else if ("expression".equals(name)) {
          throw new BuilderException("Expression based parameters are not supported yet");
        } else {
          throw new BuilderException("An invalid property '" + name + "' was found in mapping @{" + content
              + "}.  Valid properties are " + VALID_PROPERTIES);
        }
      }
      if (typeHandlerAlias != null) {
        builder.typeHandler(resolveTypeHandler(javaType, typeHandlerAlias));
      }
      return builder.build();
    }

    private Map<String, String> parseParameterMapping(String content) {
      try {
        return new ParameterExpression(content);
      } catch (BuilderException ex) {
        throw ex;
      } catch (Exception ex) {
        throw new BuilderException("Parsing error was found in mapping @{" + content
            + "}.  Check syntax #{property|(expression), var1=value1, var2=value2, ...} ", ex);
      }
    }

  }

}
