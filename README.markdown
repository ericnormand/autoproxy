# autoproxy

autoproxy defines two macros: one for creating a proxy with all
methods not explicitly defined defined as returning nil or void; and
one for creating a wrapper object that passes all method calls to the
wrapped object, except for explicitly defined methods.

See Usage for an example.

autoproxy was a macro originally written by Tim Lopez.
It can be found on the blog post: http://www.brool.com/index.php/snippet-automatic-proxy-creation-in-clojure

It was adapted by Eric Normand.

## Usage

### auto-proxy

    (auto-proxy [Object] [] 
      (toString [] "My Proxy"))

This creates a proxy object that inherits from Object and has a custom toString definition.  All other methods return nil instead of throwing an UnsupportedOperationException.

### auto-wrapper
    
    (def obj <some InputStram>)
    (auto-wrapper obj [InputStream] []
      (close [] 
        (.close obj)
        (println "I am closed!")))

This creates a proxy object of type InputStream that will print a message after it is closed.  All other methods are passed to the underlying InputStream.

## License

The MIT License

Copyright (c) 2010 Eric Normand

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

