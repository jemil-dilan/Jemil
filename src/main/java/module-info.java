@org.jspecify.annotations.NullMarked
open module cm.jemil.backend {
  requires org.jspecify;
  requires java.sql;
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.beans;
  requires spring.core;
  requires spring.web;
  requires spring.tx;
  requires spring.data.jpa;
  requires spring.data.commons;
  requires spring.security.config;
  requires spring.security.web;
  requires spring.security.core;
  requires jakarta.persistence;
  requires jakarta.validation;
  requires jakarta.annotation;
  requires com.fasterxml.jackson.annotation;
  requires static lombok;
  requires static org.mapstruct;
  requires org.slf4j;
  requires org.apache.tomcat.embed.core;
}
