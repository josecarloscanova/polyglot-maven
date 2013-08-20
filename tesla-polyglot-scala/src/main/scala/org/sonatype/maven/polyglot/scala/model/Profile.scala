/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class Profile(
               val id: String,
               val activation: Option[Activation],
               val build: Option[BuildBase],
               dependencyManagement: Option[DependencyManagement],
               dependencies: Seq[Dependency],
               distributionManagement: Option[DistributionManagement],
               modules: Seq[String],
               pluginRepositories: Seq[Repository],
               repositories: Seq[Repository]
               )
  extends
  ModelBase(
    dependencyManagement,
    dependencies,
    distributionManagement,
    modules,
    pluginRepositories,
    repositories
  )

object Profile {
  def apply(
             id: String = "default",
             activation: Activation = null,
             build: BuildBase = null,
             dependencyManagement: DependencyManagement = null,
             dependencies: Seq[Dependency] = Nil,
             distributionManagement: DistributionManagement = null,
             modules: Seq[String] = Nil,
             pluginRepositories: Seq[Repository] = Nil,
             repositories: Seq[Repository] = Nil
             ) =
    new Profile(
      id,
      Option(activation),
      Option(build),
      Option(dependencyManagement),
      dependencies,
      Option(distributionManagement),
      modules,
      pluginRepositories,
      repositories
    )
}


import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedProfile(p: Profile) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    Some(p.id).filterNot(_ == "default").foreach(args += assignString("id", _))
    p.activation.foreach(a => args += assign("activation", a.asDoc))
    p.build.foreach(b => args += assign("build", b.asDoc))
    args ++= p.asDocArgs
    `object`("Profile", args)
  }
}


import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.collection.JavaConverters._
import org.apache.maven.model.{Profile => MavenProfile}

class ConvertibleMavenProfile(mp: MavenProfile) {
  def asScala: Profile = {
    Profile(
      mp.getId,
      Option(mp.getActivation).map(_.asScala).orNull,
      Option(mp.getBuild).map(_.asScala).orNull,
      Option(mp.getDependencyManagement).map(_.asScala).orNull,
      mp.getDependencies.asScala.map(_.asScala),
      Option(mp.getDistributionManagement).map(_.asScala).orNull,
      mp.getModules.asScala,
      mp.getPluginRepositories.asScala.map(_.asScala),
      mp.getRepositories.asScala.map(_.asScala)
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaProfile(p: Profile) {
  def asJava: MavenProfile = {
    val mp = new MavenProfile
    mp.setId(p.id)
    mp.setActivation(p.activation.map(_.asJava).orNull)
    mp.setBuild(p.build.map(_.asJava).orNull)
    mp.setDependencies(p.dependencies.map(_.asJava).asJava)
    mp.setDependencyManagement(p.dependencyManagement.map(_.asJava).orNull)
    mp.setDistributionManagement(p.distributionManagement.map(_.asJava).orNull)
    mp.setModules(p.modules.asJava)
    mp.setPluginRepositories(p.pluginRepositories.map(_.asJava).asJava)
    mp.setRepositories(p.repositories.map(_.asJava).asJava)
    mp
  }
}