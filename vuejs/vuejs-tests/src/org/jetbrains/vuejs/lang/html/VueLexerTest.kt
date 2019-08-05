// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.vuejs.lang.html

import com.intellij.lang.javascript.dialects.JSLanguageLevel
import com.intellij.lexer.Lexer
import com.intellij.testFramework.LexerTestCase
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import org.jetbrains.annotations.NonNls
import org.jetbrains.vuejs.lang.html.lexer.VueLexer

open class VueLexerTest : LexerTestCase() {

  private var myFixture: IdeaProjectTestFixture? = null

  override fun setUp() {
    super.setUp()

    // needed for various XML extension points registration
    myFixture = IdeaTestFixtureFactory.getFixtureFactory()
      .createLightFixtureBuilder(LightProjectDescriptor.EMPTY_PROJECT_DESCRIPTOR).fixture
    myFixture!!.setUp()
  }

  override fun tearDown() {
    try {
      myFixture!!.tearDown()
    }
    catch (e: Throwable) {
      addSuppressedException(e)
    }
    finally {
      super.tearDown()
    }
  }

  fun testScriptEmpty() = doTest("""
    |<script>
    |</script>
  """)

  fun testScriptTS() = doTest("""
    |<script lang="typescript">
    |(() => {})();
    |</script>
  """, false)

  fun testStyleEmpty() = doTest("""
    |<style>
    |</style>
  """)

  fun testStyleSass() = doTest("""
    |<style lang="sass">
    |${'$'}font-stack:    Helvetica, sans-serif
    |${'$'}primary-color: #333
    |
    |body
    |  font: 100% ${'$'}font-stack
    |  color: ${'$'}primary-color
    |</style>
  """, false)

  fun testStyleSassAfterTemplate() = doTest("""
    |<template>
    |</template>
    |
    |<style lang="sass">
    |${'$'}font-stack:    Helvetica, sans-serif
    |${'$'}primary-color: #333
    |
    |body
    |  font: 100% ${'$'}font-stack
    |  color: ${'$'}primary-color
    |</style>
  """, false)

  fun testTemplateEmpty() = doTest("""
    |<template>
    |</template>
  """)

  fun testTemplateInner() = doTest("""
    |<template>
    |  <template></template>
    |</template>
    |<script>
    |</script>
  """)

  fun testTemplateInnerDouble() = doTest("""
    |<template>
    |  <template></template>
    |  <template></template>
    |</template>
    |<script>
    |</script>
  """)

  fun testTemplateJade() = doTest("""
    |<template lang="jade">
    |#content
    |  .block
    |    input#bar.foo1.foo2
    |</template>
  """, false)

  fun testTemplateNewLine() = doTest("""
    |<template>
    |    <q-drawer-link>
    |        text
    |    </q-drawer-link>
    |</template>
  """)

  fun testBindingAttribute() = doTest("""
    |<template>
    |  <div :bound="{foo: bar}" v-bind:bound="{bar: foo}"></div>
    |</template>
  """)

  fun testEventAttribute() = doTest("""
    |<template>
    |  <div @event="{foo: bar}" v-on:event="{bar: foo}"></div>
    |</template>
  """)

  fun testHtmlLangTemplate() = doTest("""
    |<template lang="html">
    |  <toggle :item="item"/>
    |</template>
  """, false)

  fun testVFor() = doTest("""
    |<template>
    |  <ul id="example-1">
    |    <li v-for="item in items"/>
    |    <li v-for="(item, key) in items"/>
    |  </ul>
    |</template>
  """)

  fun testLangTag() = doTest("""
    |<template>
    |  <lang >inside </lang>
    |</template>
  """)

  fun testAttributeValuesEmbedded() = doTest("""
    |<template>
    |  <div v-else class="one two three four" @click="someFun()">5</div>
    |</template>
  """)

  fun testTsxLang() = doTest("""
    |<script lang="tsx">
    |  let a = 1;
    |  export default {
    |    name: "with-tsx",
    |    render() {
    |      return <div></div>
    |    }
    |  }
    |</script>
  """, false)

  fun testScriptES6() = doTest("""
    |<script lang="typescript">
    | (() => {})();
    |</script>
  """, false)

  fun testTemplateHtml() = doTest("""
    |<template>
    |  <h2>{{title}}</h2>
    |</template>
  """)

  fun testBoundAttributes() = doTest("""
    |<template>
    | <a :src=bla() @click='event()'></a>
    |</template>
  """)

  fun testComplex() = doTest("""
    |<template>
    |  <div v-for="let contact of value; index as i"
    |    @click="contact"
    |  </div>
    |  
    |  <li v-for="let user of userObservable | async as users; index as i; first as isFirst">
    |    {{i}}/{{users.length}}. {{user}} <span v-if="isFirst">default</span>
    |  </li>
    |  
    |  <tr :style="{'visible': con}" v-for="let contact of contacts; index as i">
    |    <td>{{i + 1}}</td>
    |  </tr>
    |</template>
  """, false)

  /** Following 3 tests require fixes in JS lexer for html **/
  @Suppress("TestFunctionName")
  fun _testEscapes() = doTest("""
    |<template>
    | <div :input="'test&quot;test\u1234\u123\n\r\t'">
    | <div :input='"ttt" + &apos;str\u1234ing&apos;'>
    |</template>
  """)

  @Suppress("TestFunctionName")
  fun _testTextInEscapedQuotes() = doTest("""
    |<template>
    | <div [foo]="&quot;test&quot; + 12">
    |</template>
  """)

  @Suppress("TestFunctionName")
  fun _testTextInEscapedApos() = doTest("""
    |<template>
    | <div [foo]="&apos;test&apos; + 12">
    |</template>
  """)

  fun testScriptSrc() = doTest("""
    |<template>
    | <script src="">var i</script>
    | foo
    |</template>
  """)

  fun testScript() = doTest("""
    |<template>
    | <script>var i</script>
    | foo
    |</template>
  """)

  fun testScriptVueEvent() = doTest("""
    |<template>
    | <script @foo="">var i</script>
    | foo
    |</template>
  """)

  fun testScriptWithEventAndAngularAttr() = doTest("""
    |<template>
    | <script src="//example.com" onerror="console.log(1)" @error='console.log(1)'onload="console.log(1)" @load='console.log(1)'>
    |   console.log(2)
    | </script>
    | <div></div>
    |</template>
  """, false) // TODO improve JS embedded lexer

  fun testStyleTag() = doTest("""
    |<template>
    | <style>
    |   div {
    |   }
    | </style>
    | <div></div>
    |</template>
  """, false) // TODO improve CSS lexer to have less states

  fun testStyleVueEvent() = doTest("""
    |<template>
    | <style @load='disabled=true'>
    |    div {
    |    }
    | </style>
    | <div></div>
    |</template>
  """, false) // TODO improve CSS lexer to have less states

  fun testStyleWithEventAndBinding() = doTest("""
    |<template>
    | <style @load='disabled=true' onload="this.disabled=true" @load='disabled=true'>
    |   div {
    |   }
    | </style>
    | <div></div>
    |</template>
  """,false)// TODO improve CSS lexer to have less states

  fun testStyleAfterBinding() = doTest("""
    |<template>
    | <div :foo style="width: 13px">
    |   <span @click="foo"></span>
    | </div>
    |</template>
  """, false) // TODO improve CSS lexer to have less states

  fun testStyleAfterStyle() = doTest("""
    |<template>
    | <div style style v-foo='bar'>
    |   <span style='width: 13px' @click="foo"></span>
    | </div>
    |</template>
  """, false) // TODO improve CSS lexer to have less states

  fun testBindingAfterStyle() = doTest("""
    |<template>
    | <div style :foo='bar'>
    |  <span style='width: 13px' @click="foo"></span>
    | </div>
    |</template>
  """, false) // TODO improve CSS lexer to have less states

  fun testEmptyDirective() = doTest("""
    |<div v-foo :bar=""></div>
    |<div :foo="some"></div>
  """)

  fun testEmptyHtmlEvent() = doTest("""
    |<div onclick onclick=""></div>
    |<div :bar="some"></div>
  """)

  override fun createLexer(): Lexer = VueLexer(JSLanguageLevel.ES6)

  override fun getDirPath() = "/contrib/vuejs/vuejs-tests/testData/html/lexer"

  override fun doTest(@NonNls text: String) {
    doTest(text, true)
  }

  protected fun doTest(@NonNls text: String, checkRestartOnEveryToken: Boolean) {
    val withoutMargin = text.trimMargin()
    super.doTest(withoutMargin)
    if (checkRestartOnEveryToken) {
      checkCorrectRestartOnEveryToken(text)
    }
    else {
      checkCorrectRestart(withoutMargin)
    }
  }
}