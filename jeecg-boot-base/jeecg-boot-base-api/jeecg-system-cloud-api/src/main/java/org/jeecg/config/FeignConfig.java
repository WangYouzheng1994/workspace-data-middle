package org.jeecg.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.support.springfox.SwaggerJsonSerializer;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.PathMatcherUtil;
import org.jeecg.config.sign.interceptor.SignAuthConfiguration;
import org.jeecg.config.sign.util.HttpUtils;
import org.jeecg.config.sign.util.SignUtil;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

import feign.Feign;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (null != attributes) {
                HttpServletRequest request = attributes.getRequest();
                log.debug("Feign request: {}", request.getRequestURI());
                // ???token????????????header???
                String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
                if(token==null || "".equals(token)){
                    token = request.getParameter("token");
                }
                log.debug("Feign request token: {}", token);
                requestTemplate.header(CommonConstant.X_ACCESS_TOKEN, token);

                //??????URL?????????????????? ?????????????????????????????????
                if (PathMatcherUtil.matches(Arrays.asList(SignAuthConfiguration.urlList),requestTemplate.path())) {
                    try {
                        log.info("============================ [begin] fegin api url ============================");
                        log.info(requestTemplate.path());
                        log.info(requestTemplate.method());
                        String queryLine = requestTemplate.queryLine();
                        if(queryLine!=null && queryLine.startsWith("?")){
                            queryLine = queryLine.substring(1);
                        }
                        log.info(queryLine);
                        if(requestTemplate.body()!=null){
                            log.info(new String(requestTemplate.body()));
                        }
                        SortedMap<String, String> allParams = HttpUtils.getAllParams(requestTemplate.path(),queryLine,requestTemplate.body(),requestTemplate.method());
                        String sign = SignUtil.getParamsSign(allParams);
                        log.info(" Feign request params sign: {}",sign);
                        log.info("============================ [end] fegin api url ============================");
                        requestTemplate.header(CommonConstant.X_SIGN, sign);
                        requestTemplate.header(CommonConstant.X_TIMESTAMP, DateUtils.getCurrentTimestamp().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }



    /**
     * Feign ??????????????????????????????????????????NONE
     * Logger.Level ????????????????????????
     * NONE????????????????????????
     * BASIC???????????????????????????URL????????????????????????????????????
     * HEADERS??????????????? BASIC????????????????????????????????????????????????????????????
     * FULL?????????????????????????????????????????????????????????????????????????????????
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Feign??????????????????
     * @param messageConverters
     * @return
     */
    @Bean
    @Primary
    @Scope("prototype")
    public Encoder multipartFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    // update-begin--Author:sunjianlei Date:20210604 for??? ??? Feign ?????? FastJson ??????????????? ----------
    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(feignHttpMessageConverter());
    }

    @Bean
    public Decoder feignDecoder() {
        return new SpringDecoder(feignHttpMessageConverter());
    }

    /**
     * ??????????????????fastjson
     *
     * @return
     */
    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(this.getFastJsonConverter());
        return () -> httpMessageConverters;
    }

    private FastJsonHttpMessageConverter getFastJsonConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        List<MediaType> supportedMediaTypes = new ArrayList<>();
        MediaType mediaTypeJson = MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE);
        supportedMediaTypes.add(mediaTypeJson);
        converter.setSupportedMediaTypes(supportedMediaTypes);
        FastJsonConfig config = new FastJsonConfig();
        config.getSerializeConfig().put(JSON.class, new SwaggerJsonSerializer());
        config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        converter.setFastJsonConfig(config);

        return converter;
    }
    // update-end--Author:sunjianlei Date:20210604 for??? ??? Feign ?????? FastJson ??????????????? ----------

}
