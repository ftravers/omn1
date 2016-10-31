<div id="table-of-contents">
<h2>Table of Contents</h2>
<div id="text-table-of-contents">
<ul>
<li><a href="#sec-1">1. Tutorial 0: Initial Project</a>
<ul>
<li><a href="#sec-1-1">1.1. Explanation</a></li>
<li><a href="#sec-1-2">1.2. Testing</a></li>
</ul>
</li>
<li><a href="#sec-2">2. Tutorial 1a: Adding state and reading from it</a></li>
<li><a href="#sec-3">3. Tutorial 1b: Demo query reader decoupling</a></li>
<li><a href="#sec-4">4. Tutorial 2: Move pieces around</a></li>
<li><a href="#sec-5">5. Tutorial 3: Identity</a></li>
</ul>
</div>
</div>

TODO: try running the parser with the om/get-query Component call.

(parser {:state state} (om/get-query SomeList))
;; => [{:items [:id :title]}]

do this in the cljs REPL.

To publish this tutorial as html do:

`C-c C-e h h`

# Tutorial 0: Initial Project<a id="sec-1" name="sec-1"></a>

How to creat an absolute shell of a project:

    ╭─ ~/projects  ‹master*› 
    ╰─➤  lein new omn1
    Generating a project called omn1 based on the 'default' template.
    ...
    ╭─ ~/projects  ‹master*› 
    ╰─➤  cd omn1
    ╭─ ~/projects/omn1  ‹master*› 
    ╰─➤  git init .
    Initialized empty Git repository in /home/fenton/projects/omn1/.git/
    ╭─ ~/projects/omn1  ‹master*› 
    ╰─➤  git add .
    ╭─ ~/projects/omn1  ‹master*› 
    ╰─➤  git commit -a -m 'initial'
    [master (root-commit) 2578d78] initial
     8 files changed, 285 insertions(+)
     ...

## Explanation<a id="sec-1-1" name="sec-1-1"></a>

Now lets get a hello world app going.  There are some major
`project.clj` file updates.  I wont go into them much.  The main file
to look at is: `src/cljs/omn1/core.cljs`

The `defui` macro is how you create a component in om-next.
Components are how you manage the visual aspect of your project.  In
our simple component we are simply creating a `div` html element with
the text content being: "Hello World".

Finally we need to tie this component to the `html` page.  Here we use
the `add-root!` om function to do this.  The first argument is
a nil reconciler.  More on that later.  Then the root component, and
finally the id of the html tag where to mount this component.  See the
file `resources/public/index.html` to find the div with id "app".

## Testing<a id="sec-1-2" name="sec-1-2"></a>

I cider-jack-in this project and then run figwheel in the project to
get a cljs repl in emacs.  Then I can just go to the URL:
<http://localhost:3449/> to have a look at the results.

# Tutorial 1a: Adding state and reading from it<a id="sec-2" name="sec-2"></a>

Okay, we've really modified the `core.cljs` file here.  Lets break it
down.  First we created a `readr` function (method).  It has access to
the app state via the `env` variable: `@(:state env)`.

Stick `(.log js/console "STATE: " (str @(:state env)))` into the readr
function just before you return the `val`, to confirm this prints the
state.

`keyz`, is passed whatever is in your `query` from the component
definition.  You can log that out if you like too.

The result of the reading is stuck into the `props` that is passed to
the component.  Even though the reader returns {:value &#x2026;}, behind
the scenes, om converts the key to be :title instead.  I.e. that which
was passed to via the query to the reader becomes the key for the
props.

Thats why we access the title via: (:title &#x2026;) versus (:value &#x2026;).

Finally the rest is just wiring the different parts together.  We set
the reader in the parser.  The reconciler connects the parser to the
app state.  Finally this more full fledged reconciler is connected to
the component via the `add-root!` function.

# Tutorial 1b: Demo query reader decoupling<a id="sec-3" name="sec-3"></a>

In this version of the code we demonstrate the decoupling of the keys
of the query from what the reader returns.  Our reader now effectively
ignores the key we pass it, and we reference `:blah` only in our
component.  Okay this is a bit of a useless reader as it only ever
returns the title.

# Tutorial 2: Move pieces around<a id="sec-4" name="sec-4"></a>

Om will also run on the server side&#x2026;to a point.  The parts we have
so far of an om-next app are: the component, reading (and writing)
functions, the parser, the reconciler, and the `add-root!` function

Their jobs are as follows: the `add-root!` function connects the root
component with the reconciler.  The parser manages reading (and
writing as we'll see in a future tutorial) from the state.  The
reconciler connects the parser to the state.

We can move the parser, reading function and reconciler into a
`*.cljc` file and test it in a simple clojure repl (not a
clojurescript repl).  This allows us to use the cider debugger to step
through the code and inspect variables, nice!

We are going to start with a file/dir layout like:

    ╭─ ~/projects/omn1
    ╰─➤  tree src
    src
    `-- cljs
        `-- omn1
            `-- core.cljs

and move to:

    ╭─fenton@ss9 ~/projects/omn1  ‹master*› 
    ╰─➤  tree src
    src
    |-- cljc
    |   `-- omn1
    |       `-- recon.cljc
    `-- cljs
        `-- omn1
            `-- core.cljs

Now startup a repl (not a figwheel one) and do:

    omn1.recon> (parser {:state my-state} [:title])
    {:title "Hello World 2!"}
    omn1.recon>

So you see we passed in some query, and we can see what the results of
parsing that query with our state is.  Now we can work on a big chunk
of our Web App without having to use a browser/figwheel, thats cool!

# Tutorial 3: Identity<a id="sec-5" name="sec-5"></a>

    (recon/parser {:state recon/my-state} (om/get-query HelloWorld))

Now things start to get a bit more interesting.  The way om-next works
is that the component at the root of the tree is responsible for
getting all the data that the application requires.  Often we want to
display data that is a bit hierarchical.  So we might have an
interface that conceptually looks like:

Player: Fenton

Age: 21

Games Played:

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="left" />

<col  class="right" />
</colgroup>
<thead>
<tr>
<th scope="col" class="left">Date/Time</th>
<th scope="col" class="right">Score</th>
</tr>
</thead>

<tbody>
<tr>
<td class="left">Oct 21, 2016</td>
<td class="right">23</td>
</tr>


<tr>
<td class="left">Oct 22, 2016</td>
<td class="right">50</td>
</tr>


<tr>
<td class="left">Nov 7, 2016</td>
<td class="right">76</td>
</tr>
</tbody>
</table>

Now the way that we'll code this is that there will be a component
that displays each row, and a root component that shows the player,
age, and it will include the table of scores/dates.  So this will be a
sort of tree.

This tutorial draws inspiration from
<https://github.com/omcljs/om/wiki/Thinking-With-Links%21>