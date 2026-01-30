package ${package.Controller};

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ${package.Service}.${table.serviceName};
import ${package.Entity}.${table.entityName};
<#if swagger2>
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
</#if>
import lombok.RequiredArgsConstructor;
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>

/**
 * <p>
 * ${table.comment!} 控制器
 * </p>
 *
 * @author ${author}
 */
<#if swagger2>
@Api(tags = "${entity}管理")
</#if>
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequiredArgsConstructor
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if camelTableNameStyle??>${camelTableName}<#else>${table.entityPath}</#if>")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
  <#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
  <#else>
public class ${table.controllerName} {
  </#if>

    private final ${table.serviceName} ${cfg.camelTableName}Service;


}
</#if>
