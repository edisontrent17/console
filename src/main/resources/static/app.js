/**
 * Copyright (c) 2026 Salesforce, Inc.
 */
if (!globalThis.lwcRuntimeFlags) {
  Object.defineProperty(globalThis, 'lwcRuntimeFlags', {
    value: Object.create(null)
  });
}
if (!lwcRuntimeFlags.ENABLE_FORCE_SHADOW_MIGRATE_MODE && !lwcRuntimeFlags.DISABLE_SYNTHETIC_SHADOW) {
  /**
   * Copyright (c) 2026 Salesforce, Inc.
   */
  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  /**
   *
   * @param value
   * @param msg
   */
  function invariant(value, msg) {
    if (!value) {
      throw new Error(`Invariant Violation: ${msg}`);
    }
  }
  /**
   *
   * @param value
   * @param msg
   */
  function isTrue$1(value, msg) {
    if (!value) {
      throw new Error(`Assert Violation: ${msg}`);
    }
  }
  /**
   *
   * @param value
   * @param msg
   */
  function isFalse$1(value, msg) {
    if (value) {
      throw new Error(`Assert Violation: ${msg}`);
    }
  }
  /**
   *
   * @param msg
   */
  function fail(msg) {
    throw new Error(msg);
  }
  var assert$1 = /*#__PURE__*/Object.freeze({
    __proto__: null,
    fail: fail,
    invariant: invariant,
    isFalse: isFalse$1,
    isTrue: isTrue$1
  });

  /*
   * Copyright (c) 2024, Salesforce, Inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const {
    /** Detached {@linkcode Object.assign}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/assign MDN Reference}. */
    assign,
    /** Detached {@linkcode Object.create}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create MDN Reference}. */
    create,
    /** Detached {@linkcode Object.defineProperties}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperties MDN Reference}. */
    defineProperties,
    /** Detached {@linkcode Object.defineProperty}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperty MDN Reference}. */
    defineProperty,
    /** Detached {@linkcode Object.getOwnPropertyDescriptor}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/getOwnPropertyDescriptor MDN Reference}. */
    getOwnPropertyDescriptor,
    /** Detached {@linkcode Object.getPrototypeOf}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/getPrototypeOf MDN Reference}. */
    getPrototypeOf,
    /** Detached {@linkcode Object.hasOwnProperty}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/hasOwnProperty MDN Reference}. */
    hasOwnProperty,
    /** Detached {@linkcode Object.setPrototypeOf}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/setPrototypeOf MDN Reference}. */
    setPrototypeOf
  } = Object;
  // For some reason, JSDoc don't get picked up for multiple renamed destructured constants (even
  // though it works fine for one, e.g. isArray), so comments for these are added to the export
  // statement, rather than this declaration.
  const {
    filter: ArrayFilter,
    find: ArrayFind,
    findIndex: ArrayFindIndex,
    indexOf: ArrayIndexOf,
    map: ArrayMap,
    push: ArrayPush,
    reduce: ArrayReduce,
    reverse: ArrayReverse,
    slice: ArraySlice,
    splice: ArraySplice,
    forEach // Weird anomaly!
  } = Array.prototype;
  /**
   * Determines whether the argument is `undefined`.
   * @param obj Value to test
   * @returns `true` if the value is `undefined`.
   */
  function isUndefined(obj) {
    return obj === undefined;
  }
  /**
   * Determines whether the argument is `null`.
   * @param obj Value to test
   * @returns `true` if the value is `null`.
   */
  function isNull(obj) {
    return obj === null;
  }
  /**
   * Determines whether the argument is `true`.
   * @param obj Value to test
   * @returns `true` if the value is `true`.
   */
  function isTrue(obj) {
    return obj === true;
  }
  /**
   * Determines whether the argument is `false`.
   * @param obj Value to test
   * @returns `true` if the value is `false`.
   */
  function isFalse(obj) {
    return obj === false;
  }
  /**
   * Determines whether the argument is a function.
   * @param obj Value to test
   * @returns `true` if the value is a function.
   */
  // Replacing `Function` with a narrower type that works for all our use cases is tricky...
  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type
  function isFunction(obj) {
    return typeof obj === 'function';
  }
  /**
   * Determines whether the argument is an object or null.
   * @param obj Value to test
   * @returns `true` if the value is an object or null.
   */
  function isObject(obj) {
    return typeof obj === 'object';
  }

  /*
   * Copyright (c) 2023, Salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const KEY__SHADOW_RESOLVER = '$shadowResolver$';
  const KEY__SHADOW_RESOLVER_PRIVATE = '$$ShadowResolverKey$$';
  const KEY__SHADOW_STATIC = '$shadowStaticNode$';
  const KEY__SHADOW_STATIC_PRIVATE = '$shadowStaticNodeKey$';
  const KEY__SHADOW_TOKEN = '$shadowToken$';
  const KEY__SHADOW_TOKEN_PRIVATE = '$$ShadowTokenKey$$';
  // TODO [#3733]: remove support for legacy scope tokens
  const KEY__LEGACY_SHADOW_TOKEN = '$legacyShadowToken$';
  const KEY__LEGACY_SHADOW_TOKEN_PRIVATE = '$$LegacyShadowTokenKey$$';
  const KEY__SYNTHETIC_MODE = '$$lwc-synthetic-mode';
  const KEY__NATIVE_GET_ELEMENT_BY_ID = '$nativeGetElementById$';
  const KEY__NATIVE_QUERY_SELECTOR_ALL = '$nativeQuerySelectorAll$';
  /** version: 9.2.2 */

  /**
   * Copyright (c) 2026 Salesforce, Inc.
   */
  if (!globalThis.lwcRuntimeFlags) {
    Object.defineProperty(globalThis, 'lwcRuntimeFlags', {
      value: create(null)
    });
  }
  /** version: 9.2.2 */

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  // TODO [#2472]: Remove this workaround when appropriate.
  // eslint-disable-next-line @lwc/lwc-internal/no-global-node
  const _Node = Node;
  const nodePrototype = _Node.prototype;
  const {
    DOCUMENT_POSITION_CONTAINED_BY,
    DOCUMENT_POSITION_PRECEDING,
    DOCUMENT_POSITION_FOLLOWING,
    ELEMENT_NODE,
    TEXT_NODE,
    CDATA_SECTION_NODE,
    PROCESSING_INSTRUCTION_NODE,
    COMMENT_NODE
  } = _Node;
  const {
    appendChild,
    cloneNode,
    compareDocumentPosition,
    contains,
    getRootNode: getRootNode$1,
    insertBefore,
    removeChild,
    replaceChild,
    hasChildNodes
  } = nodePrototype;
  const firstChildGetter = getOwnPropertyDescriptor(nodePrototype, 'firstChild').get;
  const lastChildGetter = getOwnPropertyDescriptor(nodePrototype, 'lastChild').get;
  const textContentGetter = getOwnPropertyDescriptor(nodePrototype, 'textContent').get;
  const parentNodeGetter = getOwnPropertyDescriptor(nodePrototype, 'parentNode').get;
  const ownerDocumentGetter = getOwnPropertyDescriptor(nodePrototype, 'ownerDocument').get;
  const parentElementGetter = getOwnPropertyDescriptor(nodePrototype, 'parentElement').get;
  const textContextSetter = getOwnPropertyDescriptor(nodePrototype, 'textContent').set;
  const childNodesGetter = getOwnPropertyDescriptor(nodePrototype, 'childNodes').get;
  const nextSiblingGetter = getOwnPropertyDescriptor(nodePrototype, 'nextSibling').get;
  const isConnected = hasOwnProperty.call(nodePrototype, 'isConnected') ? getOwnPropertyDescriptor(nodePrototype, 'isConnected').get : function () {
    const doc = ownerDocumentGetter.call(this);
    // IE11
    return (
      // if doc is null, it means `this` is actually a document instance which
      // is always connected
      doc === null || (compareDocumentPosition.call(doc, this) & DOCUMENT_POSITION_CONTAINED_BY) !== 0
    );
  };

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const {
    getAttribute,
    getBoundingClientRect,
    getElementsByTagName: getElementsByTagName$1,
    getElementsByTagNameNS: getElementsByTagNameNS$1,
    hasAttribute,
    querySelector,
    querySelectorAll: querySelectorAll$1,
    removeAttribute,
    setAttribute
  } = Element.prototype;
  const attachShadow$1 = hasOwnProperty.call(Element.prototype, 'attachShadow') ? Element.prototype.attachShadow : () => {
    throw new TypeError('attachShadow() is not supported in current browser. Load the @lwc/synthetic-shadow polyfill and use Lightning Web Components');
  };
  const childElementCountGetter = getOwnPropertyDescriptor(Element.prototype, 'childElementCount').get;
  const firstElementChildGetter = getOwnPropertyDescriptor(Element.prototype, 'firstElementChild').get;
  const lastElementChildGetter = getOwnPropertyDescriptor(Element.prototype, 'lastElementChild').get;
  const innerTextDescriptor = getOwnPropertyDescriptor(HTMLElement.prototype, 'innerText');
  const innerTextGetter = innerTextDescriptor ? innerTextDescriptor.get : null;
  const innerTextSetter = innerTextDescriptor ? innerTextDescriptor.set : null;
  // Note: Firefox does not have outerText, https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/outerText
  const outerTextDescriptor = getOwnPropertyDescriptor(HTMLElement.prototype, 'outerText');
  const outerTextGetter = outerTextDescriptor ? outerTextDescriptor.get : null;
  const outerTextSetter = outerTextDescriptor ? outerTextDescriptor.set : null;
  const innerHTMLDescriptor = getOwnPropertyDescriptor(Element.prototype, 'innerHTML');
  const innerHTMLGetter = innerHTMLDescriptor.get;
  const innerHTMLSetter = innerHTMLDescriptor.set;
  const outerHTMLDescriptor = getOwnPropertyDescriptor(Element.prototype, 'outerHTML');
  const outerHTMLGetter = outerHTMLDescriptor.get;
  const outerHTMLSetter = outerHTMLDescriptor.set;
  const tagNameGetter = getOwnPropertyDescriptor(Element.prototype, 'tagName').get;
  const tabIndexDescriptor = getOwnPropertyDescriptor(HTMLElement.prototype, 'tabIndex');
  const tabIndexGetter = tabIndexDescriptor.get;
  const tabIndexSetter = tabIndexDescriptor.set;
  const matches = Element.prototype.matches;
  const childrenGetter = getOwnPropertyDescriptor(Element.prototype, 'children').get;
  // for IE11, access from HTMLElement
  // for all other browsers access the method from the parent Element interface
  const {
    getElementsByClassName: getElementsByClassName$1
  } = HTMLElement.prototype;
  const shadowRootGetter = hasOwnProperty.call(Element.prototype, 'shadowRoot') ? getOwnPropertyDescriptor(Element.prototype, 'shadowRoot').get : () => null;
  const assignedSlotGetter$1 = hasOwnProperty.call(Element.prototype, 'assignedSlot') ? getOwnPropertyDescriptor(Element.prototype, 'assignedSlot').get : () => null;

  /*
   * Copyright (c) 2023, Salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const assignedNodes = HTMLSlotElement.prototype.assignedNodes;
  const assignedElements = HTMLSlotElement.prototype.assignedElements;

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const eventTargetGetter = getOwnPropertyDescriptor(Event.prototype, 'target').get;
  const eventCurrentTargetGetter = getOwnPropertyDescriptor(Event.prototype, 'currentTarget').get;
  const focusEventRelatedTargetGetter = getOwnPropertyDescriptor(FocusEvent.prototype, 'relatedTarget').get;
  // IE does not implement composedPath() but that's ok because we only use this instead of our
  // composedPath() polyfill when dealing with native shadow DOM components in mixed mode. Defaulting
  // to a NOOP just to be safe, even though this is almost guaranteed to be defined such a scenario.
  const composedPath = hasOwnProperty.call(Event.prototype, 'composedPath') ? Event.prototype.composedPath : () => [];

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const DocumentPrototypeActiveElement = getOwnPropertyDescriptor(Document.prototype, 'activeElement').get;
  const elementFromPoint = Document.prototype.elementFromPoint;
  const elementsFromPoint = Document.prototype.elementsFromPoint;
  // defaultView can be null when a document has no browsing context. For example, the owner document
  // of a node in a template doesn't have a default view: https://jsfiddle.net/hv9z0q5a/
  const defaultViewGetter = getOwnPropertyDescriptor(Document.prototype, 'defaultView').get;
  const {
    querySelectorAll,
    getElementById,
    getElementsByClassName,
    getElementsByTagName,
    getElementsByTagNameNS
  } = Document.prototype;
  // In Firefox v57 and lower, getElementsByName is defined on HTMLDocument.prototype
  // In all other browsers have the method on Document.prototype
  const {
    getElementsByName
  } = HTMLDocument.prototype;

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const {
    addEventListener: windowAddEventListener,
    removeEventListener: windowRemoveEventListener
  } = window;

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  // There is code in the polyfills that requires access to the unpatched
  // Mutation Observer constructor, this the code for that.
  // Eventually, the polyfill should uses the patched version, and this file can be removed.
  const MO = MutationObserver;
  const MutationObserverObserve = MO.prototype.observe;

  /*
   * Copyright (c) 2023, Salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  // Capture the global `ShadowRoot` since synthetic shadow will override it later
  const NativeShadowRoot = ShadowRoot;
  const isInstanceOfNativeShadowRoot = node => node instanceof NativeShadowRoot;

  /*
   * Copyright (c) 2023, Salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const eventTargetPrototype = EventTarget.prototype;
  const {
    addEventListener,
    dispatchEvent,
    removeEventListener
  } = eventTargetPrototype;

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  // Used as a back reference to identify the host element
  const HostElementKey = '$$HostElementKey$$';
  const ShadowedNodeKey = '$$ShadowedNodeKey$$';
  function fastDefineProperty(node, propName, config) {
    const shadowedNode = node;
    {
      const {
        value
      } = config;
      // in prod, we prioritize performance
      shadowedNode[propName] = value;
    }
  }
  function setNodeOwnerKey(node, value) {
    fastDefineProperty(node, HostElementKey, {
      value});
  }
  function setNodeKey(node, value) {
    fastDefineProperty(node, ShadowedNodeKey, {
      value
    });
  }
  function getNodeOwnerKey(node) {
    return node[HostElementKey];
  }
  function getNodeNearestOwnerKey(node) {
    let host = node;
    let hostKey;
    // search for the first element with owner identity
    // in case of manually inserted elements and elements slotted from Light DOM
    while (!isNull(host)) {
      hostKey = getNodeOwnerKey(host);
      if (!isUndefined(hostKey)) {
        return hostKey;
      }
      host = parentNodeGetter.call(host);
      // Elements slotted from top level light DOM into synthetic shadow
      // reach the slot tag from the shadow element first
      if (!isNull(host) && isSyntheticSlotElement(host)) {
        return undefined;
      }
    }
  }
  function getNodeKey(node) {
    return node[ShadowedNodeKey];
  }
  /**
   * This function does not traverse up for performance reasons, but is sufficient for most use
   * cases. If we need to traverse up and verify those nodes that don't have owner key, use
   * isNodeDeepShadowed instead.
   * @param node
   */
  function isNodeShadowed(node) {
    return !isUndefined(getNodeOwnerKey(node));
  }

  /*
   * Copyright (c) 2024, Salesforce, Inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  // when finding a slot in the DOM, we can fold it if it is contained
  // inside another slot.
  function foldSlotElement(slot) {
    let parent = parentElementGetter.call(slot);
    while (!isNull(parent) && isSlotElement(parent)) {
      slot = parent;
      parent = parentElementGetter.call(slot);
    }
    return slot;
  }
  function isNodeSlotted(host, node) {
    const hostKey = getNodeKey(host);
    // this routine assumes that the node is coming from a different shadow (it is not owned by the host)
    // just in case the provided node is not an element
    let currentElement = node instanceof Element ? node : parentElementGetter.call(node);
    while (!isNull(currentElement) && currentElement !== host) {
      const elmOwnerKey = getNodeNearestOwnerKey(currentElement);
      const parent = parentElementGetter.call(currentElement);
      if (elmOwnerKey === hostKey) {
        // we have reached an element inside the host's template, and only if
        // that element is an slot, then the node is considered slotted
        return isSlotElement(currentElement);
      } else if (parent === host) {
        return false;
      } else if (!isNull(parent) && getNodeNearestOwnerKey(parent) !== elmOwnerKey) {
        // we are crossing a boundary of some sort since the elm and its parent
        // have different owner key. for slotted elements, this is possible
        // if the parent happens to be a slot.
        if (isSlotElement(parent)) {
          /*
           * the slot parent might be allocated inside another slot, think of:
           * <x-root> (<--- root element)
           *    <x-parent> (<--- own by x-root)
           *       <x-child> (<--- own by x-root)
           *           <slot> (<--- own by x-child)
           *               <slot> (<--- own by x-parent)
           *                  <div> (<--- own by x-root)
           *
           * while checking if x-parent has the div slotted, we need to traverse
           * up, but when finding the first slot, we skip that one in favor of the
           * most outer slot parent before jumping into its corresponding host.
           */
          currentElement = getNodeOwner(foldSlotElement(parent));
          if (!isNull(currentElement)) {
            if (currentElement === host) {
              // the slot element is a top level element inside the shadow
              // of a host that was allocated into host in question
              return true;
            } else if (getNodeNearestOwnerKey(currentElement) === hostKey) {
              // the slot element is an element inside the shadow
              // of a host that was allocated into host in question
              return true;
            }
          }
        } else {
          return false;
        }
      } else {
        currentElement = parent;
      }
    }
    return false;
  }
  function getNodeOwner(node) {
    if (!(node instanceof _Node)) {
      return null;
    }
    const ownerKey = getNodeNearestOwnerKey(node);
    if (isUndefined(ownerKey)) {
      return null;
    }
    let nodeOwner = node;
    // At this point, node is a valid node with owner identity, now we need to find the owner node
    // search for a custom element with a VM that owns the first element with owner identity attached to it
    while (!isNull(nodeOwner) && getNodeKey(nodeOwner) !== ownerKey) {
      nodeOwner = parentNodeGetter.call(nodeOwner);
    }
    if (isNull(nodeOwner)) {
      return null;
    }
    return nodeOwner;
  }
  function isSyntheticSlotElement(node) {
    return isSlotElement(node) && isNodeShadowed(node);
  }
  function isSlotElement(node) {
    return node instanceof HTMLSlotElement;
  }
  function isNodeOwnedBy(owner, node) {
    const ownerKey = getNodeNearestOwnerKey(node);
    if (isUndefined(ownerKey)) {
      // in case of root level light DOM element slotting into a synthetic shadow
      const host = parentNodeGetter.call(node);
      if (!isNull(host) && isSyntheticSlotElement(host)) {
        return false;
      }
      // in case of manually inserted elements
      return true;
    }
    return getNodeKey(owner) === ownerKey;
  }
  function shadowRootChildNodes(root) {
    const elm = getHost(root);
    return getAllMatches(elm, arrayFromCollection(childNodesGetter.call(elm)));
  }
  function getAllSlottedMatches(host, nodeList) {
    const filteredAndPatched = [];
    for (let i = 0, len = nodeList.length; i < len; i += 1) {
      const node = nodeList[i];
      if (!isNodeOwnedBy(host, node) && isNodeSlotted(host, node)) {
        ArrayPush.call(filteredAndPatched, node);
      }
    }
    return filteredAndPatched;
  }
  function getFirstSlottedMatch(host, nodeList) {
    for (let i = 0, len = nodeList.length; i < len; i += 1) {
      const node = nodeList[i];
      if (!isNodeOwnedBy(host, node) && isNodeSlotted(host, node)) {
        return node;
      }
    }
    return null;
  }
  function getAllMatches(owner, nodeList) {
    const filteredAndPatched = [];
    for (let i = 0, len = nodeList.length; i < len; i += 1) {
      const node = nodeList[i];
      const isOwned = isNodeOwnedBy(owner, node);
      if (isOwned) {
        // Patch querySelector, querySelectorAll, etc
        // if element is owned by VM
        ArrayPush.call(filteredAndPatched, node);
      }
    }
    return filteredAndPatched;
  }
  function getFirstMatch(owner, nodeList) {
    for (let i = 0, len = nodeList.length; i < len; i += 1) {
      if (isNodeOwnedBy(owner, nodeList[i])) {
        return nodeList[i];
      }
    }
    return null;
  }
  function shadowRootQuerySelector(root, selector) {
    const elm = getHost(root);
    const nodeList = arrayFromCollection(querySelectorAll$1.call(elm, selector));
    return getFirstMatch(elm, nodeList);
  }
  function shadowRootQuerySelectorAll(root, selector) {
    const elm = getHost(root);
    const nodeList = querySelectorAll$1.call(elm, selector);
    return getAllMatches(elm, arrayFromCollection(nodeList));
  }
  function getFilteredChildNodes(node) {
    if (!isSyntheticShadowHost(node) && !isSlotElement(node)) {
      // regular element - fast path
      const children = childNodesGetter.call(node);
      return arrayFromCollection(children);
    }
    if (isSyntheticShadowHost(node)) {
      // we need to get only the nodes that were slotted
      const slots = arrayFromCollection(querySelectorAll$1.call(node, 'slot'));
      const resolver = getShadowRootResolver(getShadowRoot(node));
      return ArrayReduce.call(slots,
      // @ts-expect-error Array#reduce has a generic that gets lost in our retyped ArrayReduce
      (seed, slot) => {
        if (resolver === getShadowRootResolver(slot)) {
          ArrayPush.apply(seed, getFilteredSlotAssignedNodes(slot));
        }
        return seed;
      }, []);
    } else {
      // slot element
      const children = arrayFromCollection(childNodesGetter.call(node));
      const resolver = getShadowRootResolver(node);
      return ArrayFilter.call(children, child => resolver === getShadowRootResolver(child));
    }
  }
  function getFilteredSlotAssignedNodes(slot) {
    const owner = getNodeOwner(slot);
    if (isNull(owner)) {
      return [];
    }
    const childNodes = arrayFromCollection(childNodesGetter.call(slot));
    return ArrayFilter.call(childNodes, child => !isNodeShadowed(child) || !isNodeOwnedBy(owner, child));
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  /**
  @license
  Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
  This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
  The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
  The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
  Code distributed by Google as part of the polymer project is also
  subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
  */
  // This code is inspired by Polymer ShadyDOM Polyfill
  function getInnerHTML(node) {
    let s = '';
    const childNodes = getFilteredChildNodes(node);
    for (let i = 0, len = childNodes.length; i < len; i += 1) {
      s += getOuterHTML(childNodes[i]);
    }
    return s;
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  /**
  @license
  Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
  This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
  The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
  The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
  Code distributed by Google as part of the polymer project is also
  subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
  */
  // This code is inspired by Polymer ShadyDOM Polyfill
  // http://www.whatwg.org/specs/web-apps/current-work/multipage/the-end.html#escapingString
  const escapeAttrRegExp = /[&\u00A0"]/g;
  const escapeDataRegExp = /[&\u00A0<>]/g;
  const {
    replace,
    toLowerCase
  } = String.prototype;
  function escapeReplace(c) {
    switch (c) {
      case '&':
        return '&amp;';
      case '<':
        return '&lt;';
      case '>':
        return '&gt;';
      case '"':
        return '&quot;';
      case '\u00A0':
        return '&nbsp;';
      default:
        return '';
    }
  }
  function escapeAttr(s) {
    return replace.call(s, escapeAttrRegExp, escapeReplace);
  }
  function escapeData(s) {
    return replace.call(s, escapeDataRegExp, escapeReplace);
  }
  // http://www.whatwg.org/specs/web-apps/current-work/#void-elements
  const voidElements = new Set(['AREA', 'BASE', 'BR', 'COL', 'COMMAND', 'EMBED', 'HR', 'IMG', 'INPUT', 'KEYGEN', 'LINK', 'META', 'PARAM', 'SOURCE', 'TRACK', 'WBR']);
  const plaintextParents = new Set(['STYLE', 'SCRIPT', 'XMP', 'IFRAME', 'NOEMBED', 'NOFRAMES', 'PLAINTEXT', 'NOSCRIPT']);
  function getOuterHTML(node) {
    switch (node.nodeType) {
      case ELEMENT_NODE:
        {
          const {
            attributes: attrs
          } = node;
          const tagName = tagNameGetter.call(node);
          let s = '<' + toLowerCase.call(tagName);
          for (let i = 0, attr; attr = attrs[i]; i++) {
            s += ' ' + attr.name + '="' + escapeAttr(attr.value) + '"';
          }
          s += '>';
          if (voidElements.has(tagName)) {
            return s;
          }
          return s + getInnerHTML(node) + '</' + toLowerCase.call(tagName) + '>';
        }
      case TEXT_NODE:
        {
          const {
            data,
            parentNode
          } = node;
          if (parentNode instanceof Element && plaintextParents.has(tagNameGetter.call(parentNode))) {
            return data;
          }
          return escapeData(data);
        }
      case CDATA_SECTION_NODE:
        {
          return `<!CDATA[[${node.data}]]>`;
        }
      case PROCESSING_INSTRUCTION_NODE:
        {
          return `<?${node.target} ${node.data}?>`;
        }
      case COMMENT_NODE:
        {
          return `<!--${node.data}-->`;
        }
      default:
        {
          // intentionally ignoring unknown node types
          // Note: since this routine is always invoked for childNodes
          // we can safety ignore type 9, 10 and 99 (document, fragment and doctype)
          return '';
        }
    }
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  /**
  @license
  Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
  This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
  The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
  The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
  Code distributed by Google as part of the polymer project is also
  subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
  */
  // This code is inspired by Polymer ShadyDOM Polyfill
  function getTextContent(node) {
    switch (node.nodeType) {
      case ELEMENT_NODE:
        {
          const childNodes = getFilteredChildNodes(node);
          let content = '';
          for (let i = 0, len = childNodes.length; i < len; i += 1) {
            const currentNode = childNodes[i];
            if (currentNode.nodeType !== COMMENT_NODE) {
              content += getTextContent(currentNode);
            }
          }
          return content;
        }
      default:
        return node.nodeValue;
    }
  }

  /*
   * Copyright (c) 2024, Salesforce, Inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const Items$1 = new WeakMap();
  function StaticNodeList() {
    throw new TypeError('Illegal constructor');
  }
  StaticNodeList.prototype = create(NodeList.prototype, {
    constructor: {
      writable: true,
      configurable: true,
      value: StaticNodeList
    },
    item: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(index) {
        return this[index];
      }
    },
    length: {
      enumerable: true,
      configurable: true,
      get() {
        return Items$1.get(this).length;
      }
    },
    // Iterator protocol
    forEach: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(cb, thisArg) {
        forEach.call(Items$1.get(this), cb, thisArg);
      }
    },
    entries: {
      writable: true,
      enumerable: true,
      configurable: true,
      value() {
        return ArrayMap.call(Items$1.get(this), (v, i) => [i, v]);
      }
    },
    keys: {
      writable: true,
      enumerable: true,
      configurable: true,
      value() {
        return ArrayMap.call(Items$1.get(this), (_v, i) => i);
      }
    },
    values: {
      writable: true,
      enumerable: true,
      configurable: true,
      value() {
        return Items$1.get(this);
      }
    },
    [Symbol.iterator]: {
      writable: true,
      configurable: true,
      value() {
        let nextIndex = 0;
        return {
          next: () => {
            const items = Items$1.get(this);
            return nextIndex < items.length ? {
              value: items[nextIndex++],
              done: false
            } : {
              done: true
            };
          }
        };
      }
    },
    [Symbol.toStringTag]: {
      configurable: true,
      get() {
        return 'NodeList';
      }
    },
    // IE11 doesn't support Symbol.toStringTag, in which case we
    // provide the regular toString method.
    toString: {
      writable: true,
      configurable: true,
      value() {
        return '[object NodeList]';
      }
    }
  });
  // prototype inheritance dance
  setPrototypeOf(StaticNodeList, NodeList);
  function createStaticNodeList(items) {
    const nodeList = create(StaticNodeList.prototype);
    Items$1.set(nodeList, items);
    // setting static indexes
    forEach.call(items, (item, index) => {
      defineProperty(nodeList, index, {
        value: item,
        enumerable: true,
        configurable: true
      });
    });
    return nodeList;
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  // Walk up the DOM tree, collecting all shadow roots plus the document root
  function getAllRootNodes(node) {
    const rootNodes = [];
    let currentRootNode = node.getRootNode();
    while (!isUndefined(currentRootNode)) {
      rootNodes.push(currentRootNode);
      currentRootNode = currentRootNode.host?.getRootNode();
    }
    return rootNodes;
  }
  // Keep searching up the host tree until we find an element that is within the immediate shadow root
  const findAncestorHostInImmediateShadowRoot = (rootNode, targetRootNode) => {
    let host;
    while (!isUndefined(host = rootNode.host)) {
      const thisRootNode = host.getRootNode();
      if (thisRootNode === targetRootNode) {
        return host;
      }
      rootNode = thisRootNode;
    }
  };
  function fauxElementsFromPoint(context, doc, left, top) {
    const elements = elementsFromPoint.call(doc, left, top);
    const result = [];
    const rootNodes = getAllRootNodes(context);
    // Filter the elements array to only include those elements that are in this shadow root or in one of its
    // ancestor roots. This matches Chrome and Safari's implementation (but not Firefox's, which only includes
    // elements in the immediate shadow root: https://crbug.com/1207863#c4).
    if (!isNull(elements)) {
      // can be null in IE https://developer.mozilla.org/en-US/docs/Web/API/Document/elementsFromPoint#browser_compatibility
      for (let i = 0; i < elements.length; i++) {
        const element = elements[i];
        if (isSyntheticSlotElement(element)) {
          continue;
        }
        const elementRootNode = element.getRootNode();
        if (ArrayIndexOf.call(rootNodes, elementRootNode) !== -1) {
          ArrayPush.call(result, element);
          continue;
        }
        // In cases where the host element is not visible but its shadow descendants are, then
        // we may get the shadow descendant instead of the host element here. (The
        // browser doesn't know the difference in synthetic shadow DOM.)
        // In native shadow DOM, however, elementsFromPoint would return the host but not
        // the child. So we need to detect if this shadow element's host is accessible from
        // the context's shadow root. Note we also need to be careful not to add the host
        // multiple times.
        const ancestorHost = findAncestorHostInImmediateShadowRoot(elementRootNode, rootNodes[0]);
        if (!isUndefined(ancestorHost) && ArrayIndexOf.call(elements, ancestorHost) === -1 && ArrayIndexOf.call(result, ancestorHost) === -1) {
          ArrayPush.call(result, ancestorHost);
        }
      }
    }
    return result;
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const Items = new WeakMap();
  function StaticHTMLCollection() {
    throw new TypeError('Illegal constructor');
  }
  StaticHTMLCollection.prototype = create(HTMLCollection.prototype, {
    constructor: {
      writable: true,
      configurable: true,
      value: StaticHTMLCollection
    },
    item: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(index) {
        return this[index];
      }
    },
    length: {
      enumerable: true,
      configurable: true,
      get() {
        return Items.get(this).length;
      }
    },
    // https://dom.spec.whatwg.org/#dom-htmlcollection-nameditem-key
    namedItem: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(name) {
        if (name === '') {
          return null;
        }
        const items = Items.get(this);
        for (let i = 0, len = items.length; i < len; i++) {
          const item = items[len];
          if (name === getAttribute.call(item, 'id') || name === getAttribute.call(item, 'name')) {
            return item;
          }
        }
        return null;
      }
    },
    [Symbol.toStringTag]: {
      configurable: true,
      get() {
        return 'HTMLCollection';
      }
    },
    // IE11 doesn't support Symbol.toStringTag, in which case we
    // provide the regular toString method.
    toString: {
      writable: true,
      configurable: true,
      value() {
        return '[object HTMLCollection]';
      }
    }
  });
  // prototype inheritance dance
  setPrototypeOf(StaticHTMLCollection, HTMLCollection);
  function createStaticHTMLCollection(items) {
    const collection = create(StaticHTMLCollection.prototype);
    Items.set(collection, items);
    // setting static indexes
    forEach.call(items, (item, index) => {
      defineProperty(collection, index, {
        value: item,
        enumerable: true,
        configurable: true
      });
    });
    return collection;
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const getRootNode = getRootNode$1 ??
  // Polyfill for older browsers where it's not defined
  function () {
    // eslint-disable-next-line @typescript-eslint/no-this-alias
    let node = this;
    let nodeParent = parentNodeGetter.call(node);
    while (!isNull(nodeParent)) {
      node = nodeParent;
      nodeParent = parentElementGetter.call(node);
    }
    return node;
  };
  /**
   * This method checks whether or not the content of the node is computed
   * based on the light-dom slotting mechanism. This applies to synthetic slot elements
   * and elements with shadow dom attached to them. It doesn't apply to native slot elements
   * because we don't want to patch the children getters for those elements.
   * @param node
   */
  function hasMountedChildren(node) {
    return isSyntheticSlotElement(node) || isSyntheticShadowHost(node);
  }
  function getShadowParent(node, value) {
    const owner = getNodeOwner(node);
    if (value === owner) {
      // walking up via parent chain might end up in the shadow root element
      return getShadowRoot(owner);
    } else if (value instanceof Element) {
      if (getNodeNearestOwnerKey(node) === getNodeNearestOwnerKey(value)) {
        // the element and its parent node belong to the same shadow root
        return value;
      } else if (!isNull(owner) && isSlotElement(value)) {
        // slotted elements must be top level childNodes of the slot element
        // where they slotted into, but its shadowed parent is always the
        // owner of the slot.
        const slotOwner = getNodeOwner(value);
        if (!isNull(slotOwner) && isNodeOwnedBy(owner, slotOwner)) {
          // it is a slotted element, and therefore its parent is always going to be the host of the slot
          return slotOwner;
        }
      }
    }
    return null;
  }
  function hasChildNodesPatched() {
    return getInternalChildNodes(this).length > 0;
  }
  function firstChildGetterPatched() {
    const childNodes = getInternalChildNodes(this);
    return childNodes[0] || null;
  }
  function lastChildGetterPatched() {
    const childNodes = getInternalChildNodes(this);
    return childNodes[childNodes.length - 1] || null;
  }
  function textContentGetterPatched() {
    return getTextContent(this);
  }
  function textContentSetterPatched(value) {
    textContextSetter.call(this, value);
  }
  function parentNodeGetterPatched() {
    const value = parentNodeGetter.call(this);
    if (isNull(value)) {
      return value;
    }
    // TODO [#1635]: this needs optimization, maybe implementing it based on this.assignedSlot
    return getShadowParent(this, value);
  }
  function parentElementGetterPatched() {
    const value = parentNodeGetter.call(this);
    if (isNull(value)) {
      return null;
    }
    const parentNode = getShadowParent(this, value);
    // it could be that the parentNode is the shadowRoot, in which case
    // we need to return null.
    // TODO [#1635]: this needs optimization, maybe implementing it based on this.assignedSlot
    return parentNode instanceof Element ? parentNode : null;
  }
  function containsPatched$1(otherNode) {
    if (otherNode == null || getNodeOwnerKey(this) !== getNodeOwnerKey(otherNode)) {
      // it is from another shadow
      return false;
    }
    return (compareDocumentPosition.call(this, otherNode) & DOCUMENT_POSITION_CONTAINED_BY) !== 0;
  }
  function cloneNodePatched(deep) {
    const clone = cloneNode.call(this, false);
    // Per spec, browsers only care about truthy values
    // Not strict true or false
    if (!deep) {
      return clone;
    }
    const childNodes = getInternalChildNodes(this);
    for (let i = 0, len = childNodes.length; i < len; i += 1) {
      clone.appendChild(childNodes[i].cloneNode(true));
    }
    return clone;
  }
  /**
   * This method only applies to elements with a shadow or slots
   */
  function childNodesGetterPatched() {
    if (isSyntheticShadowHost(this)) {
      const owner = getNodeOwner(this);
      const filteredChildNodes = getFilteredChildNodes(this);
      // No need to filter by owner for non-shadowed nodes
      const childNodes = isNull(owner) ? filteredChildNodes : getAllMatches(owner, filteredChildNodes);
      return createStaticNodeList(childNodes);
    }
    // nothing to do here since this does not have a synthetic shadow attached to it
    // TODO [#1636]: what about slot elements?
    return childNodesGetter.call(this);
  }
  /**
   * Get the shadow root
   * getNodeOwner() returns the host element that owns the given node
   * Note: getNodeOwner() returns null when running in native-shadow mode.
   * Fallback to using the native getRootNode() to discover the root node.
   * This is because, it is not possible to inspect the node and decide if it is part
   * of a native shadow or the synthetic shadow.
   * @param node
   */
  function getNearestRoot(node) {
    const ownerNode = getNodeOwner(node);
    if (isNull(ownerNode)) {
      // we hit a wall, either we are in native shadow mode or the node is not in lwc boundary.
      return getRootNode.call(node);
    }
    return getShadowRoot(ownerNode);
  }
  /**
   * If looking for a root node beyond shadow root by calling `node.getRootNode({composed: true})`, use the original `Node.prototype.getRootNode` method
   * to return the root of the dom tree. In IE11 and Edge, Node.prototype.getRootNode is
   * [not supported](https://developer.mozilla.org/en-US/docs/Web/API/Node/getRootNode#Browser_compatibility). The root node is discovered by manually
   * climbing up the dom tree.
   *
   * If looking for a shadow root of a node by calling `node.getRootNode({composed: false})` or `node.getRootNode()`,
   *
   * 1. Try to identify the host element that owns the give node.
   * i. Identify the shadow tree that the node belongs to
   * ii. If the node belongs to a shadow tree created by engine, return the shadowRoot of the host element that owns the shadow tree
   * 2. The host identification logic returns null in two cases:
   * i. The node does not belong to a shadow tree created by engine
   * ii. The engine is running in native shadow dom mode
   * If so, use the original Node.prototype.getRootNode to fetch the root node(or manually climb up the dom tree where getRootNode() is unsupported)
   *
   * _Spec_: https://dom.spec.whatwg.org/#dom-node-getrootnode
   * @param options
   */
  function getRootNodePatched$3(options) {
    return options?.composed ? getRootNode.call(this, options) : getNearestRoot(this);
  }
  function compareDocumentPositionPatched(otherNode) {
    if (this === otherNode) {
      return 0;
    } else if (getRootNodePatched$3.call(this) === otherNode) {
      // "this" is in a shadow tree where the shadow root is the "otherNode".
      return 10; // Node.DOCUMENT_POSITION_CONTAINS | Node.DOCUMENT_POSITION_PRECEDING
    } else if (getNodeOwnerKey(this) !== getNodeOwnerKey(otherNode)) {
      // "this" and "otherNode" belongs to 2 different shadow tree.
      return 35; // Node.DOCUMENT_POSITION_DISCONNECTED | Node.DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC | Node.DOCUMENT_POSITION_PRECEDING
    }
    // Since "this" and "otherNode" are part of the same shadow tree we can safely rely to the native
    // Node.compareDocumentPosition implementation.
    return compareDocumentPosition.call(this, otherNode);
  }
  // Non-deep-traversing patches: this descriptor map includes all descriptors that
  // do not give access to nodes beyond the immediate children.
  defineProperties(_Node.prototype, {
    firstChild: {
      get() {
        if (hasMountedChildren(this)) {
          return firstChildGetterPatched.call(this);
        }
        return firstChildGetter.call(this);
      },
      enumerable: true,
      configurable: true
    },
    lastChild: {
      get() {
        if (hasMountedChildren(this)) {
          return lastChildGetterPatched.call(this);
        }
        return lastChildGetter.call(this);
      },
      enumerable: true,
      configurable: true
    },
    textContent: {
      get() {
        // Note: we deviate from native shadow here, but are not fixing
        // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
        if (isNodeShadowed(this) || isSyntheticShadowHost(this)) {
          return textContentGetterPatched.call(this);
        }
        return textContentGetter.call(this);
      },
      set: textContentSetterPatched,
      enumerable: true,
      configurable: true
    },
    parentNode: {
      get() {
        if (isNodeShadowed(this)) {
          return parentNodeGetterPatched.call(this);
        }
        const parentNode = parentNodeGetter.call(this);
        // Handle the case where a top level light DOM element is slotted into a synthetic
        // shadow slot.
        if (!isNull(parentNode) && isSyntheticSlotElement(parentNode)) {
          return getNodeOwner(parentNode);
        }
        return parentNode;
      },
      enumerable: true,
      configurable: true
    },
    parentElement: {
      get() {
        if (isNodeShadowed(this)) {
          return parentElementGetterPatched.call(this);
        }
        const parentElement = parentElementGetter.call(this);
        // Handle the case where a top level light DOM element is slotted into a synthetic
        // shadow slot.
        if (!isNull(parentElement) && isSyntheticSlotElement(parentElement)) {
          return getNodeOwner(parentElement);
        }
        return parentElement;
      },
      enumerable: true,
      configurable: true
    },
    childNodes: {
      get() {
        if (hasMountedChildren(this)) {
          return childNodesGetterPatched.call(this);
        }
        return childNodesGetter.call(this);
      },
      enumerable: true,
      configurable: true
    },
    hasChildNodes: {
      value() {
        if (hasMountedChildren(this)) {
          return hasChildNodesPatched.call(this);
        }
        return hasChildNodes.call(this);
      },
      enumerable: true,
      writable: true,
      configurable: true
    },
    compareDocumentPosition: {
      value(otherNode) {
        // Note: we deviate from native shadow here, but are not fixing
        // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
        if (isGlobalPatchingSkipped(this)) {
          return compareDocumentPosition.call(this, otherNode);
        }
        return compareDocumentPositionPatched.call(this, otherNode);
      },
      enumerable: true,
      writable: true,
      configurable: true
    },
    contains: {
      value(otherNode) {
        // 1. Node.prototype.contains() returns true if otherNode is an inclusive descendant
        //    spec: https://dom.spec.whatwg.org/#dom-node-contains
        // 2. This normalizes the behavior of this api across all browsers.
        //    In IE11, a disconnected dom element without children invoking contains() on self, returns false
        if (this === otherNode) {
          return true;
        }
        // Note: we deviate from native shadow here, but are not fixing
        // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
        if (otherNode == null) {
          return false;
        }
        if (isNodeShadowed(this) || isSyntheticShadowHost(this)) {
          return containsPatched$1.call(this, otherNode);
        }
        return contains.call(this, otherNode);
      },
      enumerable: true,
      writable: true,
      configurable: true
    },
    cloneNode: {
      value(deep) {
        // Note: we deviate from native shadow here, but are not fixing
        // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
        if (isNodeShadowed(this) || isSyntheticShadowHost(this)) {
          return cloneNodePatched.call(this, deep);
        }
        return cloneNode.call(this, deep);
      },
      enumerable: true,
      writable: true,
      configurable: true
    },
    getRootNode: {
      value: getRootNodePatched$3,
      enumerable: true,
      configurable: true,
      writable: true
    },
    isConnected: {
      enumerable: true,
      configurable: true,
      get() {
        return isConnected.call(this);
      }
    }
  });
  const getInternalChildNodes = function (node) {
    return node.childNodes;
  };
  // IE11 extra patches for wrong prototypes
  if (hasOwnProperty.call(HTMLElement.prototype, 'contains')) {
    defineProperty(HTMLElement.prototype, 'contains', getOwnPropertyDescriptor(_Node.prototype, 'contains'));
  }
  if (hasOwnProperty.call(HTMLElement.prototype, 'parentElement')) {
    defineProperty(HTMLElement.prototype, 'parentElement', getOwnPropertyDescriptor(_Node.prototype, 'parentElement'));
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const EventListenerMap = new WeakMap();
  const ComposedPathMap = new WeakMap();
  function isEventListenerOrEventListenerObject$1(fnOrObj) {
    return isFunction(fnOrObj) || isObject(fnOrObj) && !isNull(fnOrObj) && isFunction(fnOrObj.handleEvent);
  }
  function shouldInvokeListener(event, target, currentTarget) {
    // Subsequent logic assumes that `currentTarget` must be contained in the composed path for the listener to be
    // invoked, but this is not always the case. `composedPath()` will sometimes return an empty array, even when the
    // listener should be invoked (e.g., a disconnected instance of EventTarget, an instance of XMLHttpRequest, etc).
    if (target === currentTarget) {
      return true;
    }
    let composedPath = ComposedPathMap.get(event);
    if (isUndefined(composedPath)) {
      composedPath = event.composedPath();
      ComposedPathMap.set(event, composedPath);
    }
    return composedPath.includes(currentTarget);
  }
  function getEventListenerWrapper(fnOrObj) {
    if (!isEventListenerOrEventListenerObject$1(fnOrObj)) {
      return fnOrObj;
    }
    let wrapperFn = EventListenerMap.get(fnOrObj);
    if (isUndefined(wrapperFn)) {
      wrapperFn = function (event) {
        // This function is invoked from an event listener and currentTarget is always defined.
        const currentTarget = eventCurrentTargetGetter.call(event);
        const actualTarget = getActualTarget(event);
        if (!shouldInvokeListener(event, actualTarget, currentTarget)) {
          return;
        }
        return isFunction(fnOrObj) ? fnOrObj.call(this, event) : fnOrObj.handleEvent && fnOrObj.handleEvent(event);
      };
      EventListenerMap.set(fnOrObj, wrapperFn);
    }
    return wrapperFn;
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const eventToContextMap = new WeakMap();
  function getEventHandler(listener) {
    if (isFunction(listener)) {
      return listener;
    } else {
      return listener.handleEvent;
    }
  }
  function isEventListenerOrEventListenerObject(listener) {
    return isFunction(listener) || isFunction(listener?.handleEvent);
  }
  const customElementToWrappedListeners = new WeakMap();
  function getEventMap(elm) {
    let listenerInfo = customElementToWrappedListeners.get(elm);
    if (isUndefined(listenerInfo)) {
      listenerInfo = create(null);
      customElementToWrappedListeners.set(elm, listenerInfo);
    }
    return listenerInfo;
  }
  /**
   * Events dispatched on shadow roots actually end up being dispatched on their hosts. This means that the event.target
   * property of events dispatched on shadow roots always resolve to their host. This function understands this
   * abstraction and properly returns a reference to the shadow root when appropriate.
   * @param event
   */
  function getActualTarget(event) {
    return eventToShadowRootMap.get(event) ?? eventTargetGetter.call(event);
  }
  const shadowRootEventListenerMap = new WeakMap();
  function getManagedShadowRootListener(listener) {
    if (!isEventListenerOrEventListenerObject(listener)) {
      throw new TypeError(); // avoiding problems with non-valid listeners
    }
    let managedListener = shadowRootEventListenerMap.get(listener);
    if (isUndefined(managedListener)) {
      managedListener = {
        identity: listener,
        placement: 1 /* EventListenerContext.SHADOW_ROOT_LISTENER */,
        handleEvent(event) {
          // currentTarget is always defined inside an event listener
          let currentTarget = eventCurrentTargetGetter.call(event);
          // If currentTarget is not an instance of a native shadow root then we're dealing with a
          // host element whose synthetic shadow root must be accessed via getShadowRoot().
          if (!isInstanceOfNativeShadowRoot(currentTarget)) {
            currentTarget = getShadowRoot(currentTarget);
          }
          const actualTarget = getActualTarget(event);
          if (shouldInvokeListener(event, actualTarget, currentTarget)) {
            getEventHandler(listener).call(currentTarget, event);
          }
        }
      };
      shadowRootEventListenerMap.set(listener, managedListener);
    }
    return managedListener;
  }
  const customElementEventListenerMap = new WeakMap();
  function getManagedCustomElementListener(listener) {
    if (!isEventListenerOrEventListenerObject(listener)) {
      throw new TypeError(); // avoiding problems with non-valid listeners
    }
    let managedListener = customElementEventListenerMap.get(listener);
    if (isUndefined(managedListener)) {
      managedListener = {
        identity: listener,
        placement: 0 /* EventListenerContext.CUSTOM_ELEMENT_LISTENER */,
        handleEvent(event) {
          // currentTarget is always defined inside an event listener
          const currentTarget = eventCurrentTargetGetter.call(event);
          const actualTarget = getActualTarget(event);
          if (shouldInvokeListener(event, actualTarget, currentTarget)) {
            getEventHandler(listener).call(currentTarget, event);
          }
        }
      };
      customElementEventListenerMap.set(listener, managedListener);
    }
    return managedListener;
  }
  function indexOfManagedListener(listeners, listener) {
    return ArrayFindIndex.call(listeners, l => l.identity === listener.identity);
  }
  function domListener(evt) {
    let immediatePropagationStopped = false;
    let propagationStopped = false;
    const {
      type,
      stopImmediatePropagation,
      stopPropagation
    } = evt;
    // currentTarget is always defined
    const currentTarget = eventCurrentTargetGetter.call(evt);
    const listenerMap = getEventMap(currentTarget);
    const listeners = listenerMap[type]; // it must have listeners at this point
    defineProperty(evt, 'stopImmediatePropagation', {
      value() {
        immediatePropagationStopped = true;
        stopImmediatePropagation.call(evt);
      },
      writable: true,
      enumerable: true,
      configurable: true
    });
    defineProperty(evt, 'stopPropagation', {
      value() {
        propagationStopped = true;
        stopPropagation.call(evt);
      },
      writable: true,
      enumerable: true,
      configurable: true
    });
    // in case a listener adds or removes other listeners during invocation
    const bookkeeping = ArraySlice.call(listeners);
    function invokeListenersByPlacement(placement) {
      forEach.call(bookkeeping, listener => {
        if (isFalse(immediatePropagationStopped) && listener.placement === placement) {
          // making sure that the listener was not removed from the original listener queue
          if (indexOfManagedListener(listeners, listener) !== -1) {
            // all handlers on the custom element should be called with undefined 'this'
            listener.handleEvent.call(undefined, evt);
          }
        }
      });
    }
    eventToContextMap.set(evt, 1 /* EventListenerContext.SHADOW_ROOT_LISTENER */);
    invokeListenersByPlacement(1 /* EventListenerContext.SHADOW_ROOT_LISTENER */);
    if (isFalse(immediatePropagationStopped) && isFalse(propagationStopped)) {
      // doing the second iteration only if the first one didn't interrupt the event propagation
      eventToContextMap.set(evt, 0 /* EventListenerContext.CUSTOM_ELEMENT_LISTENER */);
      invokeListenersByPlacement(0 /* EventListenerContext.CUSTOM_ELEMENT_LISTENER */);
    }
    eventToContextMap.set(evt, 2 /* EventListenerContext.UNKNOWN_LISTENER */);
  }
  function attachDOMListener(elm, type, managedListener) {
    const listenerMap = getEventMap(elm);
    let listeners = listenerMap[type];
    if (isUndefined(listeners)) {
      listeners = listenerMap[type] = [];
    }
    // Prevent identical listeners from subscribing to the same event type.
    // TODO [#1824]: Options will also play a factor in deduping if we introduce options support
    if (indexOfManagedListener(listeners, managedListener) !== -1) {
      return;
    }
    // only add to DOM if there is no other listener on the same placement yet
    if (listeners.length === 0) {
      addEventListener.call(elm, type, domListener);
    }
    ArrayPush.call(listeners, managedListener);
  }
  function detachDOMListener(elm, type, managedListener) {
    const listenerMap = getEventMap(elm);
    let index;
    let listeners;
    if (!isUndefined(listeners = listenerMap[type]) && (index = indexOfManagedListener(listeners, managedListener)) !== -1) {
      ArraySplice.call(listeners, index, 1);
      // only remove from DOM if there is no other listener on the same placement
      if (listeners.length === 0) {
        removeEventListener.call(elm, type, domListener);
      }
    }
  }
  function addCustomElementEventListener(type, listener, _options) {
    if (isEventListenerOrEventListenerObject(listener)) {
      const managedListener = getManagedCustomElementListener(listener);
      attachDOMListener(this, type, managedListener);
    }
  }
  function removeCustomElementEventListener(type, listener, _options) {
    if (isEventListenerOrEventListenerObject(listener)) {
      const managedListener = getManagedCustomElementListener(listener);
      detachDOMListener(this, type, managedListener);
    }
  }
  function addShadowRootEventListener(sr, type, listener, _options) {
    if (isEventListenerOrEventListenerObject(listener)) {
      const elm = getHost(sr);
      const managedListener = getManagedShadowRootListener(listener);
      attachDOMListener(elm, type, managedListener);
    }
  }
  function removeShadowRootEventListener(sr, type, listener, _options) {
    if (isEventListenerOrEventListenerObject(listener)) {
      const elm = getHost(sr);
      const managedListener = getManagedShadowRootListener(listener);
      detachDOMListener(elm, type, managedListener);
    }
  }

  /*
   * Copyright (c) 2023, Salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const getRootNodePatched$2 = _Node.prototype.getRootNode;
  assert$1.isFalse(String(getRootNodePatched$2).includes('[native code]'), 'Node prototype must be patched before patching shadow root.');
  const InternalSlot = new WeakMap();
  const {
    createDocumentFragment
  } = document;
  function hasInternalSlot(root) {
    return InternalSlot.has(root);
  }
  function getInternalSlot(root) {
    const record = InternalSlot.get(root);
    if (isUndefined(record)) {
      throw new TypeError();
    }
    return record;
  }
  defineProperty(_Node.prototype, KEY__SHADOW_RESOLVER, {
    set(fn) {
      if (isUndefined(fn)) return;
      this[KEY__SHADOW_RESOLVER_PRIVATE] = fn;
      // TODO [#1164]: temporary propagation of the key
      setNodeOwnerKey(this, fn.nodeKey);
    },
    get() {
      return this[KEY__SHADOW_RESOLVER_PRIVATE];
    },
    configurable: true,
    enumerable: true
  });
  // The isUndefined check is because two copies of synthetic shadow may be loaded on the same page, and this
  // would throw an error if we tried to redefine it. Plus the whole point is to expose the native method.
  if (isUndefined(globalThis[KEY__NATIVE_GET_ELEMENT_BY_ID])) {
    defineProperty(globalThis, KEY__NATIVE_GET_ELEMENT_BY_ID, {
      value: getElementById,
      configurable: true
    });
  }
  // See note above.
  if (isUndefined(globalThis[KEY__NATIVE_QUERY_SELECTOR_ALL])) {
    defineProperty(globalThis, KEY__NATIVE_QUERY_SELECTOR_ALL, {
      value: querySelectorAll,
      configurable: true
    });
  }
  function getShadowRootResolver(node) {
    return node[KEY__SHADOW_RESOLVER];
  }
  function setShadowRootResolver(node, fn) {
    node[KEY__SHADOW_RESOLVER] = fn;
  }
  function isDelegatingFocus(host) {
    return getInternalSlot(host).delegatesFocus;
  }
  function getHost(root) {
    return getInternalSlot(root).host;
  }
  function getShadowRoot(elm) {
    return getInternalSlot(elm).shadowRoot;
  }
  // Intentionally adding `Node` here in addition to `Element` since this check is harmless for nodes
  // and we can avoid having to cast the type before calling this method in a few places.
  function isSyntheticShadowHost(node) {
    const shadowRootRecord = InternalSlot.get(node);
    return !isUndefined(shadowRootRecord) && node === shadowRootRecord.host;
  }
  function isSyntheticShadowRoot(node) {
    const shadowRootRecord = InternalSlot.get(node);
    return !isUndefined(shadowRootRecord) && node === shadowRootRecord.shadowRoot;
  }
  let uid = 0;
  function attachShadow(elm, options) {
    if (InternalSlot.has(elm)) {
      throw new Error(`Failed to execute 'attachShadow' on 'Element': Shadow root cannot be created on a host which already hosts a shadow tree.`);
    }
    const {
      mode,
      delegatesFocus
    } = options;
    // creating a real fragment for shadowRoot instance
    const doc = getOwnerDocument(elm);
    const sr = createDocumentFragment.call(doc);
    // creating shadow internal record
    const record = {
      mode,
      delegatesFocus: !!delegatesFocus,
      host: elm,
      shadowRoot: sr
    };
    InternalSlot.set(sr, record);
    InternalSlot.set(elm, record);
    const shadowResolver = () => sr;
    const x = shadowResolver.nodeKey = uid++;
    setNodeKey(elm, x);
    setShadowRootResolver(sr, shadowResolver);
    // correcting the proto chain
    setPrototypeOf(sr, SyntheticShadowRoot.prototype);
    return sr;
  }
  // Defined separately from others because it's used in `compareDocumentPosition`
  function containsPatched(otherNode) {
    if (this === otherNode) {
      return true;
    }
    const host = getHost(this);
    // must be child of the host and owned by it.
    return (compareDocumentPosition.call(host, otherNode) & DOCUMENT_POSITION_CONTAINED_BY) !== 0 && isNodeOwnedBy(host, otherNode);
  }
  const SyntheticShadowRootDescriptors = {
    constructor: {
      writable: true,
      configurable: true,
      value: SyntheticShadowRoot
    },
    toString: {
      writable: true,
      configurable: true,
      value() {
        return `[object ShadowRoot]`;
      }
    },
    synthetic: {
      writable: false,
      enumerable: false,
      configurable: false,
      value: true
    }
  };
  const ShadowRootDescriptors = {
    activeElement: {
      enumerable: true,
      configurable: true,
      get() {
        const host = getHost(this);
        const doc = getOwnerDocument(host);
        const activeElement = DocumentPrototypeActiveElement.call(doc);
        if (isNull(activeElement)) {
          return activeElement;
        }
        if ((compareDocumentPosition.call(host, activeElement) & DOCUMENT_POSITION_CONTAINED_BY) === 0) {
          return null;
        }
        // activeElement must be child of the host and owned by it
        let node = activeElement;
        while (!isNodeOwnedBy(host, node)) {
          // parentElement is always an element because we are talking up the tree knowing
          // that it is a child of the host.
          node = parentElementGetter.call(node);
        }
        // If we have a slot element here that means that we were dealing
        // with an element that was passed to one of our slots. In this
        // case, activeElement returns null.
        if (isSlotElement(node)) {
          return null;
        }
        return node;
      }
    },
    delegatesFocus: {
      configurable: true,
      get() {
        return getInternalSlot(this).delegatesFocus;
      }
    },
    elementFromPoint: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(left, top) {
        const host = getHost(this);
        const doc = getOwnerDocument(host);
        return fauxElementFromPoint(this, doc, left, top);
      }
    },
    elementsFromPoint: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(left, top) {
        const host = getHost(this);
        const doc = getOwnerDocument(host);
        return fauxElementsFromPoint(this, doc, left, top);
      }
    },
    getSelection: {
      writable: true,
      enumerable: true,
      configurable: true,
      value() {
        throw new Error('Disallowed method "getSelection" on ShadowRoot.');
      }
    },
    host: {
      enumerable: true,
      configurable: true,
      get() {
        return getHost(this);
      }
    },
    mode: {
      configurable: true,
      get() {
        return getInternalSlot(this).mode;
      }
    },
    styleSheets: {
      enumerable: true,
      configurable: true,
      get() {
        throw new Error();
      }
    }
  };
  const eventToShadowRootMap = new WeakMap();
  const NodePatchDescriptors = {
    insertBefore: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(newChild, refChild) {
        insertBefore.call(getHost(this), newChild, refChild);
        return newChild;
      }
    },
    removeChild: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(oldChild) {
        removeChild.call(getHost(this), oldChild);
        return oldChild;
      }
    },
    appendChild: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(newChild) {
        appendChild.call(getHost(this), newChild);
        return newChild;
      }
    },
    replaceChild: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(newChild, oldChild) {
        replaceChild.call(getHost(this), newChild, oldChild);
        return oldChild;
      }
    },
    addEventListener: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(type, listener, options) {
        addShadowRootEventListener(this, type, listener);
      }
    },
    dispatchEvent: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(evt) {
        eventToShadowRootMap.set(evt, this);
        // Typescript does not like it when you treat the `arguments` object as an array
        // @ts-expect-error type-mismatch
        return dispatchEvent.apply(getHost(this), arguments);
      }
    },
    removeEventListener: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(type, listener, options) {
        removeShadowRootEventListener(this, type, listener);
      }
    },
    baseURI: {
      enumerable: true,
      configurable: true,
      get() {
        return getHost(this).baseURI;
      }
    },
    childNodes: {
      enumerable: true,
      configurable: true,
      get() {
        return createStaticNodeList(shadowRootChildNodes(this));
      }
    },
    cloneNode: {
      writable: true,
      enumerable: true,
      configurable: true,
      value() {
        throw new Error('Disallowed method "cloneNode" on ShadowRoot.');
      }
    },
    compareDocumentPosition: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(otherNode) {
        const host = getHost(this);
        if (this === otherNode) {
          // "this" and "otherNode" are the same shadow root.
          return 0;
        } else if (containsPatched.call(this, otherNode)) {
          // "otherNode" belongs to the shadow tree where "this" is the shadow root.
          return 20; // Node.DOCUMENT_POSITION_CONTAINED_BY | Node.DOCUMENT_POSITION_FOLLOWING
        } else if (compareDocumentPosition.call(host, otherNode) & DOCUMENT_POSITION_CONTAINED_BY) {
          // "otherNode" is in a different shadow tree contained by the shadow tree where "this" is the shadow root.
          return 37; // Node.DOCUMENT_POSITION_DISCONNECTED | Node.DOCUMENT_POSITION_FOLLOWING | Node.DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC
        } else {
          // "otherNode" is in a different shadow tree that is not contained by the shadow tree where "this" is the shadow root.
          return 35; // Node.DOCUMENT_POSITION_DISCONNECTED | Node.DOCUMENT_POSITION_PRECEDING | Node.DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC
        }
      }
    },
    contains: {
      writable: true,
      enumerable: true,
      configurable: true,
      value: containsPatched
    },
    firstChild: {
      enumerable: true,
      configurable: true,
      get() {
        const childNodes = getInternalChildNodes(this);
        return childNodes[0] || null;
      }
    },
    lastChild: {
      enumerable: true,
      configurable: true,
      get() {
        const childNodes = getInternalChildNodes(this);
        return childNodes[childNodes.length - 1] || null;
      }
    },
    hasChildNodes: {
      writable: true,
      enumerable: true,
      configurable: true,
      value() {
        const childNodes = getInternalChildNodes(this);
        return childNodes.length > 0;
      }
    },
    isConnected: {
      enumerable: true,
      configurable: true,
      get() {
        return isConnected.call(getHost(this));
      }
    },
    nextSibling: {
      enumerable: true,
      configurable: true,
      get() {
        return null;
      }
    },
    previousSibling: {
      enumerable: true,
      configurable: true,
      get() {
        return null;
      }
    },
    nodeName: {
      enumerable: true,
      configurable: true,
      get() {
        return '#document-fragment';
      }
    },
    nodeType: {
      enumerable: true,
      configurable: true,
      get() {
        return 11; // Node.DOCUMENT_FRAGMENT_NODE
      }
    },
    nodeValue: {
      enumerable: true,
      configurable: true,
      get() {
        return null;
      }
    },
    ownerDocument: {
      enumerable: true,
      configurable: true,
      get() {
        return getHost(this).ownerDocument;
      }
    },
    parentElement: {
      enumerable: true,
      configurable: true,
      get() {
        return null;
      }
    },
    parentNode: {
      enumerable: true,
      configurable: true,
      get() {
        return null;
      }
    },
    textContent: {
      enumerable: true,
      configurable: true,
      get() {
        const childNodes = getInternalChildNodes(this);
        let textContent = '';
        for (let i = 0, len = childNodes.length; i < len; i += 1) {
          const currentNode = childNodes[i];
          if (currentNode.nodeType !== COMMENT_NODE) {
            textContent += getTextContent(currentNode);
          }
        }
        return textContent;
      },
      set(v) {
        const host = getHost(this);
        textContextSetter.call(host, v);
      }
    },
    // Since the synthetic shadow root is a detached DocumentFragment, short-circuit the getRootNode behavior
    getRootNode: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(options) {
        return isTrue(options?.composed) ? getRootNodePatched$2.call(getHost(this), {
          composed: true
        }) : this;
      }
    }
  };
  const ElementPatchDescriptors = {
    innerHTML: {
      enumerable: true,
      configurable: true,
      get() {
        const childNodes = getInternalChildNodes(this);
        let innerHTML = '';
        for (let i = 0, len = childNodes.length; i < len; i += 1) {
          innerHTML += getOuterHTML(childNodes[i]);
        }
        return innerHTML;
      },
      set(v) {
        const host = getHost(this);
        innerHTMLSetter.call(host, v);
      }
    }
  };
  const ParentNodePatchDescriptors = {
    childElementCount: {
      enumerable: true,
      configurable: true,
      get() {
        return this.children.length;
      }
    },
    children: {
      enumerable: true,
      configurable: true,
      get() {
        return createStaticHTMLCollection(ArrayFilter.call(shadowRootChildNodes(this), elm => elm instanceof Element));
      }
    },
    firstElementChild: {
      enumerable: true,
      configurable: true,
      get() {
        return this.children[0] || null;
      }
    },
    lastElementChild: {
      enumerable: true,
      configurable: true,
      get() {
        const {
          children
        } = this;
        return children.item(children.length - 1) || null;
      }
    },
    getElementById: {
      writable: true,
      enumerable: true,
      configurable: true,
      value() {
        throw new Error('Disallowed method "getElementById" on ShadowRoot.');
      }
    },
    querySelector: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(selectors) {
        return shadowRootQuerySelector(this, selectors);
      }
    },
    querySelectorAll: {
      writable: true,
      enumerable: true,
      configurable: true,
      value(selectors) {
        return createStaticNodeList(shadowRootQuerySelectorAll(this, selectors));
      }
    }
  };
  assign(SyntheticShadowRootDescriptors, NodePatchDescriptors, ParentNodePatchDescriptors, ElementPatchDescriptors, ShadowRootDescriptors);
  function SyntheticShadowRoot() {
    throw new TypeError('Illegal constructor');
  }
  SyntheticShadowRoot.prototype = create(DocumentFragment.prototype, SyntheticShadowRootDescriptors);
  // `this.shadowRoot instanceof ShadowRoot` should evaluate to true even for synthetic shadow
  defineProperty(SyntheticShadowRoot, Symbol.hasInstance, {
    value: function (object) {
      // Technically we should walk up the entire prototype chain, but with SyntheticShadowRoot
      // it's reasonable to assume that no one is doing any deep subclasses here.
      return isObject(object) && !isNull(object) && (isInstanceOfNativeShadowRoot(object) || getPrototypeOf(object) === SyntheticShadowRoot.prototype);
    }
  });

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  function isSyntheticOrNativeShadowRoot(node) {
    return isSyntheticShadowRoot(node) || isInstanceOfNativeShadowRoot(node);
  }
  // Helpful for tests running with jsdom
  function getOwnerDocument(node) {
    const doc = ownerDocumentGetter.call(node);
    // if doc is null, it means `this` is actually a document instance
    return doc === null ? node : doc;
  }
  function getOwnerWindow(node) {
    const doc = getOwnerDocument(node);
    const win = defaultViewGetter.call(doc);
    if (win === null) {
      // this method should never be called with a node that is not part
      // of a qualifying connected node.
      throw new TypeError();
    }
    return win;
  }
  let skipGlobalPatching;
  // Note: we deviate from native shadow here, but are not fixing
  // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
  function isGlobalPatchingSkipped(node) {
    // we lazily compute this value instead of doing it during evaluation, this helps
    // for apps that are setting this after the engine code is evaluated.
    if (isUndefined(skipGlobalPatching)) {
      const ownerDocument = getOwnerDocument(node);
      skipGlobalPatching = ownerDocument.body && getAttribute.call(ownerDocument.body, 'data-global-patching-bypass') === 'temporary-bypass';
    }
    return isTrue(skipGlobalPatching);
  }
  function arrayFromCollection(collection) {
    const size = collection.length;
    const cloned = [];
    if (size > 0) {
      for (let i = 0; i < size; i++) {
        cloned[i] = collection[i];
      }
    }
    return cloned;
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  /**
  @license
  Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
  This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
  The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
  The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
  Code distributed by Google as part of the polymer project is also
  subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
  */
  function pathComposer(startNode, composed) {
    const composedPath = [];
    let startRoot;
    if (startNode instanceof Window) {
      startRoot = startNode;
    } else if (startNode instanceof _Node) {
      startRoot = startNode.getRootNode();
    } else {
      return composedPath;
    }
    let current = startNode;
    while (!isNull(current)) {
      composedPath.push(current);
      if (current instanceof Element || current instanceof Text) {
        const assignedSlot = current.assignedSlot;
        if (!isNull(assignedSlot)) {
          current = assignedSlot;
        } else {
          current = current.parentNode;
        }
      } else if (isSyntheticOrNativeShadowRoot(current) && (composed || current !== startRoot)) {
        current = current.host;
      } else if (current instanceof _Node) {
        current = current.parentNode;
      } else {
        // could be Window
        current = null;
      }
    }
    let doc;
    if (startNode instanceof Window) {
      doc = startNode.document;
    } else {
      doc = getOwnerDocument(startNode);
    }
    // event composedPath includes window when startNode's ownerRoot is document
    if (composedPath[composedPath.length - 1] === doc) {
      composedPath.push(window);
    }
    return composedPath;
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  /**
  @license
  Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
  This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
  The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
  The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
  Code distributed by Google as part of the polymer project is also
  subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
  */
  function retarget(refNode, path) {
    if (isNull(refNode)) {
      return null;
    }
    // If ANCESTOR's root is not a shadow root or ANCESTOR's root is BASE's
    // shadow-including inclusive ancestor, return ANCESTOR.
    const refNodePath = pathComposer(refNode, true);
    const p$ = path;
    for (let i = 0, ancestor, lastRoot, root, rootIdx; i < p$.length; i++) {
      ancestor = p$[i];
      root = ancestor instanceof Window ? ancestor : ancestor.getRootNode();
      // Retarget to ancestor if ancestor is not shadowed
      if (!isSyntheticOrNativeShadowRoot(root)) {
        return ancestor;
      }
      if (root !== lastRoot) {
        rootIdx = refNodePath.indexOf(root);
        lastRoot = root;
      }
      // Retarget to ancestor if ancestor is shadowed by refNode's shadow root
      if (!isUndefined(rootIdx) && rootIdx > -1) {
        return ancestor;
      }
    }
    return null;
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  function fauxElementFromPoint(context, doc, left, top) {
    const element = elementFromPoint.call(doc, left, top);
    if (isNull(element)) {
      return element;
    }
    return retarget(context, pathComposer(element, true));
  }

  /*
   * Copyright (c) 2024, Salesforce, Inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  function elemFromPoint(left, top) {
    return fauxElementFromPoint(this, this, left, top);
  }
  Document.prototype.elementFromPoint = elemFromPoint;
  function elemsFromPoint(left, top) {
    return fauxElementsFromPoint(this, this, left, top);
  }
  Document.prototype.elementsFromPoint = elemsFromPoint;
  // Go until we reach to top of the LWC tree
  defineProperty(Document.prototype, 'activeElement', {
    get() {
      let node = DocumentPrototypeActiveElement.call(this);
      if (isNull(node)) {
        return node;
      }
      while (!isUndefined(getNodeOwnerKey(node))) {
        node = parentElementGetter.call(node);
        if (isNull(node)) {
          return null;
        }
      }
      if (node.tagName === 'HTML') {
        // IE 11. Active element should never be html element
        node = this.body;
      }
      return node;
    },
    enumerable: true,
    configurable: true
  });
  // The following patched methods hide shadowed elements from global
  // traversing mechanisms. They are simplified for performance reasons to
  // filter by ownership and do not account for slotted elements. This
  // compromise is fine for our synthetic shadow dom because root elements
  // cannot have slotted elements.
  // Another compromise here is that all these traversing methods will return
  // static HTMLCollection or static NodeList. We decided that this compromise
  // is not a big problem considering the amount of code that is relying on
  // the liveliness of these results are rare.
  defineProperty(Document.prototype, 'getElementById', {
    value() {
      const elm = getElementById.apply(this, ArraySlice.call(arguments));
      if (isNull(elm)) {
        return null;
      }
      // Note: we deviate from native shadow here, but are not fixing
      // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
      return isUndefined(getNodeOwnerKey(elm)) || isGlobalPatchingSkipped(elm) ? elm : null;
    },
    writable: true,
    enumerable: true,
    configurable: true
  });
  defineProperty(Document.prototype, 'querySelector', {
    value() {
      const elements = arrayFromCollection(querySelectorAll.apply(this, ArraySlice.call(arguments)));
      const filtered = ArrayFind.call(elements,
      // Note: we deviate from native shadow here, but are not fixing
      // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
      elm => isUndefined(getNodeOwnerKey(elm)) || isGlobalPatchingSkipped(elm));
      return !isUndefined(filtered) ? filtered : null;
    },
    writable: true,
    enumerable: true,
    configurable: true
  });
  defineProperty(Document.prototype, 'querySelectorAll', {
    value() {
      const elements = arrayFromCollection(querySelectorAll.apply(this, ArraySlice.call(arguments)));
      const filtered = ArrayFilter.call(elements,
      // Note: we deviate from native shadow here, but are not fixing
      // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
      elm => isUndefined(getNodeOwnerKey(elm)) || isGlobalPatchingSkipped(elm));
      return createStaticNodeList(filtered);
    },
    writable: true,
    enumerable: true,
    configurable: true
  });
  defineProperty(Document.prototype, 'getElementsByClassName', {
    value() {
      const elements = arrayFromCollection(getElementsByClassName.apply(this, ArraySlice.call(arguments)));
      const filtered = ArrayFilter.call(elements,
      // Note: we deviate from native shadow here, but are not fixing
      // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
      elm => isUndefined(getNodeOwnerKey(elm)) || isGlobalPatchingSkipped(elm));
      return createStaticHTMLCollection(filtered);
    },
    writable: true,
    enumerable: true,
    configurable: true
  });
  defineProperty(Document.prototype, 'getElementsByTagName', {
    value() {
      const elements = arrayFromCollection(getElementsByTagName.apply(this, ArraySlice.call(arguments)));
      const filtered = ArrayFilter.call(elements,
      // Note: we deviate from native shadow here, but are not fixing
      // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
      elm => isUndefined(getNodeOwnerKey(elm)) || isGlobalPatchingSkipped(elm));
      return createStaticHTMLCollection(filtered);
    },
    writable: true,
    enumerable: true,
    configurable: true
  });
  defineProperty(Document.prototype, 'getElementsByTagNameNS', {
    value() {
      const elements = arrayFromCollection(getElementsByTagNameNS.apply(this, ArraySlice.call(arguments)));
      const filtered = ArrayFilter.call(elements,
      // Note: we deviate from native shadow here, but are not fixing
      // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
      elm => isUndefined(getNodeOwnerKey(elm)) || isGlobalPatchingSkipped(elm));
      return createStaticHTMLCollection(filtered);
    },
    writable: true,
    enumerable: true,
    configurable: true
  });
  defineProperty(
  // In Firefox v57 and lower, getElementsByName is defined on HTMLDocument.prototype
  getOwnPropertyDescriptor(HTMLDocument.prototype, 'getElementsByName') ? HTMLDocument.prototype : Document.prototype, 'getElementsByName', {
    value() {
      const elements = arrayFromCollection(getElementsByName.apply(this, ArraySlice.call(arguments)));
      const filtered = ArrayFilter.call(elements,
      // Note: we deviate from native shadow here, but are not fixing
      // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
      elm => isUndefined(getNodeOwnerKey(elm)) || isGlobalPatchingSkipped(elm));
      return createStaticNodeList(filtered);
    },
    writable: true,
    enumerable: true,
    configurable: true
  });

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  Object.defineProperty(window, 'ShadowRoot', {
    value: SyntheticShadowRoot,
    configurable: true,
    writable: true
  });

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const CustomEventConstructor = CustomEvent;
  function PatchedCustomEvent(type, eventInitDict) {
    const event = new CustomEventConstructor(type, eventInitDict);
    const isComposed = !!(eventInitDict && eventInitDict.composed);
    Object.defineProperties(event, {
      composed: {
        get() {
          return isComposed;
        },
        configurable: true,
        enumerable: true
      }
    });
    return event;
  }
  PatchedCustomEvent.prototype = CustomEventConstructor.prototype;
  window.CustomEvent = PatchedCustomEvent;

  /*
   * Copyright (c) 2023, Salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  // Note that ClipboardEvent is undefined in Jest/jsdom
  // See: https://github.com/jsdom/jsdom/issues/1568
  if (typeof ClipboardEvent !== 'undefined') {
    const isComposedType = assign(create(null), {
      copy: 1,
      cut: 1,
      paste: 1
    });
    // Patch the prototype to override the composed property on user-agent dispatched events
    defineProperties(ClipboardEvent.prototype, {
      composed: {
        get() {
          const {
            type
          } = this;
          return isComposedType[type] === 1;
        },
        configurable: true,
        enumerable: true
      }
    });
  }

  /*
   * Copyright (c) 2024, Salesforce, Inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const OriginalMutationObserver = MutationObserver;
  const {
    disconnect: originalDisconnect,
    observe: originalObserve,
    takeRecords: originalTakeRecords
  } = OriginalMutationObserver.prototype;
  // Internal fields to maintain relationships
  const wrapperLookupField = '$$lwcObserverCallbackWrapper$$';
  const observerLookupField = '$$lwcNodeObservers$$';
  const observerToNodesMap = new WeakMap();
  function getNodeObservers(node) {
    return node[observerLookupField];
  }
  function setNodeObservers(node, observers) {
    node[observerLookupField] = observers;
  }
  /**
   * Retarget the mutation record's target value to its shadowRoot
   * @param originalRecord
   */
  function retargetMutationRecord(originalRecord) {
    const {
      addedNodes,
      removedNodes,
      target,
      type
    } = originalRecord;
    const retargetedRecord = create(MutationRecord.prototype);
    defineProperties(retargetedRecord, {
      addedNodes: {
        get() {
          return addedNodes;
        },
        enumerable: true,
        configurable: true
      },
      removedNodes: {
        get() {
          return removedNodes;
        },
        enumerable: true,
        configurable: true
      },
      type: {
        get() {
          return type;
        },
        enumerable: true,
        configurable: true
      },
      target: {
        get() {
          return target.shadowRoot;
        },
        enumerable: true,
        configurable: true
      }
    });
    return retargetedRecord;
  }
  /**
   * Utility to identify if a target node is being observed by the given observer
   * Start at the current node, if the observer is registered to observe the current node, the mutation qualifies
   * @param observer
   * @param target
   */
  function isQualifiedObserver(observer, target) {
    let parentNode = target;
    while (!isNull(parentNode)) {
      const parentNodeObservers = getNodeObservers(parentNode);
      if (!isUndefined(parentNodeObservers) && (parentNodeObservers[0] === observer ||
      // perf optimization to check for the first item is a match
      ArrayIndexOf.call(parentNodeObservers, observer) !== -1)) {
        return true;
      }
      parentNode = parentNode.parentNode;
    }
    return false;
  }
  /**
   * This function provides a shadow dom compliant filtered view of mutation records for a given observer.
   *
   * The key logic here is to determine if a given observer has been registered to observe any nodes
   * between the target node of a mutation record to the target's root node.
   * This function also retargets records when mutations occur directly under the shadow root
   * @param mutations
   * @param observer
   */
  function filterMutationRecords(mutations, observer) {
    const result = [];
    for (const record of mutations) {
      const {
        target,
        type
      } = record;
      // If target is an lwc host,
      // Determine if the mutations affected the host or the shadowRoot
      // Mutations affecting host: changes to slot content
      // Mutations affecting shadowRoot: changes to template content
      if (type === 'childList' && !isUndefined(getNodeKey(target))) {
        const {
          addedNodes
        } = record;
        // In case of added nodes, we can climb up the tree and determine eligibility
        if (addedNodes.length > 0) {
          // Optimization: Peek in and test one node to decide if the MutationRecord qualifies
          // The remaining nodes in this MutationRecord will have the same ownerKey
          const sampleNode = addedNodes[0];
          if (isQualifiedObserver(observer, sampleNode)) {
            // If the target was being observed, then return record as-is
            // this will be the case for slot content
            const nodeObservers = getNodeObservers(target);
            if (nodeObservers && (nodeObservers[0] === observer || ArrayIndexOf.call(nodeObservers, observer) !== -1)) {
              ArrayPush.call(result, record);
            } else {
              // else, must be observing the shadowRoot
              ArrayPush.call(result, retargetMutationRecord(record));
            }
          }
        } else {
          const {
            removedNodes
          } = record;
          // In the case of removed nodes, climbing the tree is not an option as the nodes are disconnected
          // We can only check if either the host or shadow root was observed and qualify the record
          const shadowRoot = target.shadowRoot;
          const sampleNode = removedNodes[0];
          if (getNodeNearestOwnerKey(target) === getNodeNearestOwnerKey(sampleNode) &&
          // trickery: sampleNode is slot content
          isQualifiedObserver(observer, target) // use target as a close enough reference to climb up
          ) {
            ArrayPush.call(result, record);
          } else if (shadowRoot) {
            const shadowRootObservers = getNodeObservers(shadowRoot);
            if (shadowRootObservers && (shadowRootObservers[0] === observer || ArrayIndexOf.call(shadowRootObservers, observer) !== -1)) {
              ArrayPush.call(result, retargetMutationRecord(record));
            }
          }
        }
      } else {
        // Mutation happened under a root node(shadow root or document) and the decision is straighforward
        // Ascend the tree starting from target and check if observer is qualified
        if (isQualifiedObserver(observer, target)) {
          ArrayPush.call(result, record);
        }
      }
    }
    return result;
  }
  function getWrappedCallback(callback) {
    let wrappedCallback = callback[wrapperLookupField];
    if (isUndefined(wrappedCallback)) {
      wrappedCallback = callback[wrapperLookupField] = (mutations, observer) => {
        // Filter mutation records
        const filteredRecords = filterMutationRecords(mutations, observer);
        // If not records are eligible for the observer, do not invoke callback
        if (filteredRecords.length === 0) {
          return;
        }
        callback.call(observer, filteredRecords, observer);
      };
    }
    return wrappedCallback;
  }
  /**
   * Patched MutationObserver constructor.
   * 1. Wrap the callback to filter out MutationRecords based on dom ownership
   * 2. Add a property field to track all observed targets of the observer instance
   * @param callback
   */
  function PatchedMutationObserver(callback) {
    const wrappedCallback = getWrappedCallback(callback);
    const observer = new OriginalMutationObserver(wrappedCallback);
    return observer;
  }
  function patchedDisconnect() {
    originalDisconnect.call(this);
    // Clear the node to observer reference which is a strong references
    const observedNodes = observerToNodesMap.get(this);
    if (!isUndefined(observedNodes)) {
      forEach.call(observedNodes, observedNode => {
        const observers = observedNode[observerLookupField];
        if (!isUndefined(observers)) {
          const index = ArrayIndexOf.call(observers, this);
          if (index !== -1) {
            ArraySplice.call(observers, index, 1);
          }
        }
      });
      observedNodes.length = 0;
    }
  }
  /**
   * A single mutation observer can observe multiple nodes(target).
   * Maintain a list of all targets that the observer chooses to observe
   * @param target
   * @param options
   */
  function patchedObserve(target, options) {
    let targetObservers = getNodeObservers(target);
    // Maintain a list of all observers that want to observe a node
    if (isUndefined(targetObservers)) {
      targetObservers = [];
      setNodeObservers(target, targetObservers);
    }
    // Same observer trying to observe the same node
    if (ArrayIndexOf.call(targetObservers, this) === -1) {
      ArrayPush.call(targetObservers, this);
    } // else There is more bookkeeping to do here https://dom.spec.whatwg.org/#dom-mutationobserver-observe Step #7
    // SyntheticShadowRoot instances are not actually a part of the DOM so observe the host instead.
    if (isSyntheticShadowRoot(target)) {
      target = target.host;
    }
    // maintain a list of all nodes observed by this observer
    if (observerToNodesMap.has(this)) {
      const observedNodes = observerToNodesMap.get(this);
      if (ArrayIndexOf.call(observedNodes, target) === -1) {
        ArrayPush.call(observedNodes, target);
      }
    } else {
      observerToNodesMap.set(this, [target]);
    }
    return originalObserve.call(this, target, options);
  }
  /**
   * Patch the takeRecords() api to filter MutationRecords based on the observed targets
   */
  function patchedTakeRecords() {
    return filterMutationRecords(originalTakeRecords.call(this), this);
  }
  PatchedMutationObserver.prototype = OriginalMutationObserver.prototype;
  PatchedMutationObserver.prototype.disconnect = patchedDisconnect;
  PatchedMutationObserver.prototype.observe = patchedObserve;
  PatchedMutationObserver.prototype.takeRecords = patchedTakeRecords;
  defineProperty(window, 'MutationObserver', {
    value: PatchedMutationObserver,
    configurable: true,
    writable: true
  });

  /*
   * Copyright (c) 2024, Salesforce, Inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const getRootNodePatched$1 = _Node.prototype.getRootNode;
  assert$1.isFalse(String(getRootNodePatched$1).includes('[native code]'), 'Node prototype must be patched before event target.');
  function patchedAddEventListener(type, listener, optionsOrCapture) {
    if (isSyntheticShadowHost(this)) {
      // Typescript does not like it when you treat the `arguments` object as an array
      // @ts-expect-error type-mismatch
      return addCustomElementEventListener.apply(this, arguments);
    }
    if (this instanceof _Node && isInstanceOfNativeShadowRoot(getRootNodePatched$1.call(this))) {
      // Typescript does not like it when you treat the `arguments` object as an array
      // @ts-expect-error type-mismatch
      return addEventListener.apply(this, arguments);
    }
    if (arguments.length < 2) {
      // Slow path, unlikely to be called frequently. We expect modern browsers to throw:
      // https://googlechrome.github.io/samples/event-listeners-mandatory-arguments/
      const args = ArraySlice.call(arguments);
      if (args.length > 1) {
        args[1] = getEventListenerWrapper(args[1]);
      }
      // Ignore types because we're passing through to native method
      // @ts-expect-error type-mismatch
      return addEventListener.apply(this, args);
    }
    // Fast path. This function is optimized to avoid ArraySlice because addEventListener is called
    // very frequently, and it provides a measurable perf boost to avoid so much array cloning.
    const wrappedListener = getEventListenerWrapper(listener);
    // The third argument is optional, so passing in `undefined` for `optionsOrCapture` gives capture=false
    return addEventListener.call(this, type, wrappedListener, optionsOrCapture);
  }
  function patchedRemoveEventListener(_type, _listener, _optionsOrCapture) {
    if (isSyntheticShadowHost(this)) {
      // Typescript does not like it when you treat the `arguments` object as an array
      // @ts-expect-error type-mismatch
      return removeCustomElementEventListener.apply(this, arguments);
    }
    const args = ArraySlice.call(arguments);
    if (arguments.length > 1) {
      args[1] = getEventListenerWrapper(args[1]);
    }
    // Ignore types because we're passing through to native method
    // @ts-expect-error type-mismatch
    removeEventListener.apply(this, args);
    // Account for listeners that were added before this polyfill was applied
    // Typescript does not like it when you treat the `arguments` object as an array
    // @ts-expect-error type-mismatch
    removeEventListener.apply(this, arguments);
  }
  defineProperties(eventTargetPrototype, {
    addEventListener: {
      value: patchedAddEventListener,
      enumerable: true,
      writable: true,
      configurable: true
    },
    removeEventListener: {
      value: patchedRemoveEventListener,
      enumerable: true,
      writable: true,
      configurable: true
    }
  });

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  function patchedCurrentTargetGetter() {
    const currentTarget = eventCurrentTargetGetter.call(this);
    if (isNull(currentTarget)) {
      return null;
    }
    if (eventToContextMap.get(this) === 1 /* EventListenerContext.SHADOW_ROOT_LISTENER */) {
      return getShadowRoot(currentTarget);
    }
    return currentTarget;
  }
  function patchedTargetGetter() {
    const originalTarget = eventTargetGetter.call(this);
    if (!(originalTarget instanceof _Node)) {
      return originalTarget;
    }
    const doc = getOwnerDocument(originalTarget);
    const composedPath = pathComposer(originalTarget, this.composed);
    const originalCurrentTarget = eventCurrentTargetGetter.call(this);
    // Handle cases where the currentTarget is null (for async events), and when an event has been
    // added to Window
    if (!(originalCurrentTarget instanceof _Node)) {
      // TODO [#1511]: Special escape hatch to support legacy behavior. Should be fixed.
      // If the event's target is being accessed async and originalTarget is not a keyed element, do not retarget
      if (isNull(originalCurrentTarget) && isUndefined(getNodeOwnerKey(originalTarget))) {
        return originalTarget;
      }
      return retarget(doc, composedPath);
    } else if (originalCurrentTarget === doc || originalCurrentTarget === doc.body) {
      if (isUndefined(getNodeOwnerKey(originalTarget))) {
        return originalTarget;
      }
      return retarget(doc, composedPath);
    }
    let actualCurrentTarget = originalCurrentTarget;
    let actualPath = composedPath;
    // Address the possibility that `currentTarget` is a shadow root
    if (isSyntheticShadowHost(originalCurrentTarget)) {
      const context = eventToContextMap.get(this);
      if (context === 1 /* EventListenerContext.SHADOW_ROOT_LISTENER */) {
        actualCurrentTarget = getShadowRoot(originalCurrentTarget);
      }
    }
    // Address the possibility that `target` is a shadow root
    if (isSyntheticShadowHost(originalTarget) && eventToShadowRootMap.has(this)) {
      actualPath = pathComposer(getShadowRoot(originalTarget), this.composed);
    }
    return retarget(actualCurrentTarget, actualPath);
  }
  function patchedComposedPathValue() {
    const originalTarget = eventTargetGetter.call(this);
    // Account for events with targets that are not instances of Node (e.g., when a readystatechange
    // handler is listening on an instance of XMLHttpRequest).
    if (!(originalTarget instanceof _Node)) {
      return [];
    }
    // If the original target is inside a native shadow root, then just call the native
    // composePath() method. The event is already retargeted and this causes our composedPath()
    // polyfill to compute the wrong value. This is only an issue when you have a native web
    // component inside an LWC component (see test in same commit) but this scenario is unlikely
    // because we don't yet support that. Workaround specifically for W-9846457. Mixed mode solution
    // will likely be more involved.
    const hasShadowRoot = Boolean(originalTarget.shadowRoot);
    const hasSyntheticShadowRootAttached = hasInternalSlot(originalTarget);
    if (hasShadowRoot && !hasSyntheticShadowRootAttached) {
      return composedPath.call(this);
    }
    const originalCurrentTarget = eventCurrentTargetGetter.call(this);
    // If the event has completed propagation, the composedPath should be an empty array.
    if (isNull(originalCurrentTarget)) {
      return [];
    }
    // Address the possibility that `target` is a shadow root
    let actualTarget = originalTarget;
    if (isSyntheticShadowHost(originalTarget) && eventToShadowRootMap.has(this)) {
      actualTarget = getShadowRoot(originalTarget);
    }
    return pathComposer(actualTarget, this.composed);
  }
  defineProperties(Event.prototype, {
    target: {
      get: patchedTargetGetter,
      enumerable: true,
      configurable: true
    },
    currentTarget: {
      get: patchedCurrentTargetGetter,
      enumerable: true,
      configurable: true
    },
    composedPath: {
      value: patchedComposedPathValue,
      writable: true,
      enumerable: true,
      configurable: true
    },
    // Non-standard but widely supported for backwards-compatibility
    srcElement: {
      get: patchedTargetGetter,
      enumerable: true,
      configurable: true
    },
    // Non-standard but implemented in Chrome and continues to exist for backwards-compatibility
    // https://source.chromium.org/chromium/chromium/src/+/master:third_party/blink/renderer/core/dom/events/event.idl;l=58?q=event.idl&ss=chromium
    path: {
      get: patchedComposedPathValue,
      enumerable: true,
      configurable: true
    }
  });

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  function retargetRelatedTarget(Ctor) {
    const relatedTargetGetter = getOwnPropertyDescriptor(Ctor.prototype, 'relatedTarget').get;
    defineProperty(Ctor.prototype, 'relatedTarget', {
      get() {
        const relatedTarget = relatedTargetGetter.call(this);
        if (isNull(relatedTarget)) {
          return null;
        }
        if (!(relatedTarget instanceof _Node) || !isNodeShadowed(relatedTarget)) {
          return relatedTarget;
        }
        let pointOfReference = eventCurrentTargetGetter.call(this);
        if (isNull(pointOfReference)) {
          pointOfReference = getOwnerDocument(relatedTarget);
        }
        return retarget(pointOfReference, pathComposer(relatedTarget, true));
      },
      enumerable: true,
      configurable: true
    });
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  retargetRelatedTarget(FocusEvent);

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  retargetRelatedTarget(MouseEvent);

  /*
   * Copyright (c) 2021, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const assignedSlotGetter = hasOwnProperty.call(Text.prototype, 'assignedSlot') ? getOwnPropertyDescriptor(Text.prototype, 'assignedSlot').get : () => null;

  /*
   * Copyright (c) 2024, Salesforce, Inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  // We can use a single observer without having to worry about leaking because
  // "Registered observers in a node’s registered observer list have a weak
  // reference to the node."
  // https://dom.spec.whatwg.org/#garbage-collection
  let observer;
  const observerConfig = {
    childList: true
  };
  const SlotChangeKey = new WeakMap();
  function initSlotObserver() {
    return new MO(mutations => {
      const slots = [];
      forEach.call(mutations, mutation => {
        const {
          target: slot
        } = mutation;
        if (ArrayIndexOf.call(slots, slot) === -1) {
          ArrayPush.call(slots, slot);
          dispatchEvent.call(slot, new CustomEvent('slotchange'));
        }
      });
    });
  }
  function getFilteredSlotFlattenNodes(slot) {
    const childNodes = arrayFromCollection(childNodesGetter.call(slot));
    return ArrayReduce.call(childNodes,
    // @ts-expect-error Array#reduce has a generic that is lost by our redefined ArrayReduce
    (seed, child) => {
      if (child instanceof Element && isSlotElement(child)) {
        ArrayPush.apply(seed, getFilteredSlotFlattenNodes(child));
      } else {
        ArrayPush.call(seed, child);
      }
      return seed;
    }, []);
  }
  function assignedSlotGetterPatched() {
    const parentNode = parentNodeGetter.call(this);
    // use original assignedSlot if parent has a native shdow root
    if (parentNode instanceof Element) {
      const sr = shadowRootGetter.call(parentNode);
      if (isInstanceOfNativeShadowRoot(sr)) {
        if (this instanceof Text) {
          return assignedSlotGetter.call(this);
        }
        return assignedSlotGetter$1.call(this);
      }
    }
    /**
     * The node is assigned to a slot if:
     * - it has a parent and its parent is a slot element
     * - and if the slot owner key is different than the node owner key.
     * When the slot and the slotted node are 2 different shadow trees, the owner keys will be
     * different. When the slot is in a shadow tree and the slotted content is a light DOM node,
     * the light DOM node doesn't have an owner key and therefor the slot owner key will be
     * different than the node owner key (always `undefined`).
     */
    if (!isNull(parentNode) && isSlotElement(parentNode) && getNodeOwnerKey(parentNode) !== getNodeOwnerKey(this)) {
      return parentNode;
    }
    return null;
  }
  defineProperties(HTMLSlotElement.prototype, {
    addEventListener: {
      value(type, listener, options) {
        // super.addEventListener - but that doesn't work with typescript
        HTMLElement.prototype.addEventListener.call(this, type, listener, options);
        if (type === 'slotchange' && !SlotChangeKey.get(this)) {
          SlotChangeKey.set(this, true);
          if (!observer) {
            observer = initSlotObserver();
          }
          MutationObserverObserve.call(observer, this, observerConfig);
        }
      },
      writable: true,
      enumerable: true,
      configurable: true
    },
    assignedElements: {
      value(options) {
        if (isNodeShadowed(this)) {
          const flatten = !isUndefined(options) && isTrue(options.flatten);
          const nodes = flatten ? getFilteredSlotFlattenNodes(this) : getFilteredSlotAssignedNodes(this);
          return ArrayFilter.call(nodes, node => node instanceof Element);
        } else {
          return assignedElements.apply(this, ArraySlice.call(arguments));
        }
      },
      writable: true,
      enumerable: true,
      configurable: true
    },
    assignedNodes: {
      value(options) {
        if (isNodeShadowed(this)) {
          const flatten = !isUndefined(options) && isTrue(options.flatten);
          return flatten ? getFilteredSlotFlattenNodes(this) : getFilteredSlotAssignedNodes(this);
        } else {
          return assignedNodes.apply(this, ArraySlice.call(arguments));
        }
      },
      writable: true,
      enumerable: true,
      configurable: true
    },
    name: {
      get() {
        const name = getAttribute.call(this, 'name');
        return isNull(name) ? '' : name;
      },
      set(value) {
        setAttribute.call(this, 'name', value);
      },
      enumerable: true,
      configurable: true
    },
    childNodes: {
      get() {
        if (isNodeShadowed(this)) {
          const owner = getNodeOwner(this);
          const childNodes = isNull(owner) ? [] : getAllMatches(owner, getFilteredChildNodes(this));
          return createStaticNodeList(childNodes);
        }
        return childNodesGetter.call(this);
      },
      enumerable: true,
      configurable: true
    }
  });

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  // Non-deep-traversing patches: this descriptor map includes all descriptors that
  // do not five access to nodes beyond the immediate children.
  defineProperties(Text.prototype, {
    assignedSlot: {
      get: assignedSlotGetterPatched,
      enumerable: true,
      configurable: true
    }
  });

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  /**
   * This methods filters out elements that are not in the same shadow root of context.
   * It does not enforce shadow dom semantics if $context is not managed by LWC
   * @param context
   * @param unfilteredNodes
   */
  function getNonPatchedFilteredArrayOfNodes(context, unfilteredNodes) {
    let filtered;
    const ownerKey = getNodeOwnerKey(context);
    // a node inside a shadow.
    if (!isUndefined(ownerKey)) {
      if (isSyntheticShadowHost(context)) {
        // element with shadowRoot attached
        const owner = getNodeOwner(context);
        if (isNull(owner)) {
          filtered = [];
        } else if (getNodeKey(context)) {
          // it is a custom element, and we should then filter by slotted elements
          filtered = getAllSlottedMatches(context, unfilteredNodes);
        } else {
          // regular element, we should then filter by ownership
          filtered = getAllMatches(owner, unfilteredNodes);
        }
      } else {
        // context is handled by lwc, using getNodeNearestOwnerKey to include manually inserted elements in the same shadow.
        filtered = ArrayFilter.call(unfilteredNodes, elm => getNodeNearestOwnerKey(elm) === ownerKey);
      }
    } else if (context instanceof HTMLBodyElement) {
      // `context` is document.body which is already patched.
      filtered = ArrayFilter.call(unfilteredNodes,
      // Note: we deviate from native shadow here, but are not fixing
      // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
      elm => isUndefined(getNodeOwnerKey(elm)) || isGlobalPatchingSkipped(context));
    } else {
      // `context` is outside the lwc boundary, return unfiltered list.
      filtered = ArraySlice.call(unfilteredNodes);
    }
    return filtered;
  }

  /*
   * Copyright (c) 2024, Salesforce, Inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  function innerHTMLGetterPatched() {
    const childNodes = getInternalChildNodes(this);
    let innerHTML = '';
    for (let i = 0, len = childNodes.length; i < len; i += 1) {
      innerHTML += getOuterHTML(childNodes[i]);
    }
    return innerHTML;
  }
  function outerHTMLGetterPatched() {
    return getOuterHTML(this);
  }
  // Capture the browser's native error message for duplicate attachShadow calls
  // so the guard below throws an identical error regardless of browser.
  const nativeAttachShadowErrorMessage = (() => {
    const el = document.createElement('div');
    el.attachShadow({
      mode: 'open'
    });
    try {
      el.attachShadow({
        mode: 'open'
      });
    } catch ({
      message
    }) {
      return message;
    }
    return '';
  })();
  function attachShadowPatched(options) {
    // To retain native behavior of the API, provide synthetic shadowRoot only when specified
    if (options[KEY__SYNTHETIC_MODE]) {
      return attachShadow(this, options);
    }
    // LWC hosts already use a synthetic shadow root. Without this guard, native
    // attachShadow would still succeed and attach a second (native) shadow tree,
    // which violates the one-shadow-per-element model this polyfill assumes and
    // leaves that subtree on a different patching path than synthetic shadow.
    if (!lwcRuntimeFlags.DISABLE_HOST_ATTACH_SHADOW_GUARD && hasInternalSlot(this)) {
      throw new Error(nativeAttachShadowErrorMessage);
    }
    return attachShadow$1.call(this, options);
  }
  function shadowRootGetterPatched() {
    if (isSyntheticShadowHost(this)) {
      const shadow = getShadowRoot(this);
      if (shadow.mode === 'open') {
        return shadow;
      }
    }
    return shadowRootGetter.call(this);
  }
  function childrenGetterPatched() {
    const owner = getNodeOwner(this);
    const filteredChildNodes = getFilteredChildNodes(this);
    // No need to filter by owner for non-shadowed nodes
    const childNodes = isNull(owner) ? filteredChildNodes : getAllMatches(owner, filteredChildNodes);
    return createStaticHTMLCollection(ArrayFilter.call(childNodes, node => node instanceof Element));
  }
  function childElementCountGetterPatched() {
    return this.children.length;
  }
  function firstElementChildGetterPatched() {
    return this.children[0] || null;
  }
  function lastElementChildGetterPatched() {
    const {
      children
    } = this;
    return children.item(children.length - 1) || null;
  }
  // Non-deep-traversing patches: this descriptor map includes all descriptors that
  // do not five access to nodes beyond the immediate children.
  defineProperties(Element.prototype, {
    innerHTML: {
      get() {
        // Note: we deviate from native shadow here, but are not fixing
        // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
        if (isNodeShadowed(this) || isSyntheticShadowHost(this)) {
          return innerHTMLGetterPatched.call(this);
        }
        return innerHTMLGetter.call(this);
      },
      set(v) {
        innerHTMLSetter.call(this, v);
      },
      enumerable: true,
      configurable: true
    },
    outerHTML: {
      get() {
        // Note: we deviate from native shadow here, but are not fixing
        // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
        if (isNodeShadowed(this) || isSyntheticShadowHost(this)) {
          return outerHTMLGetterPatched.call(this);
        }
        return outerHTMLGetter.call(this);
      },
      set(v) {
        outerHTMLSetter.call(this, v);
      },
      enumerable: true,
      configurable: true
    },
    attachShadow: {
      value: attachShadowPatched,
      enumerable: true,
      writable: true,
      configurable: true
    },
    shadowRoot: {
      get: shadowRootGetterPatched,
      enumerable: true,
      configurable: true
    },
    // patched in HTMLElement if exists (IE11 is the one off here)
    children: {
      get() {
        if (hasMountedChildren(this)) {
          return childrenGetterPatched.call(this);
        }
        return childrenGetter.call(this);
      },
      enumerable: true,
      configurable: true
    },
    childElementCount: {
      get() {
        if (hasMountedChildren(this)) {
          return childElementCountGetterPatched.call(this);
        }
        return childElementCountGetter.call(this);
      },
      enumerable: true,
      configurable: true
    },
    firstElementChild: {
      get() {
        if (hasMountedChildren(this)) {
          return firstElementChildGetterPatched.call(this);
        }
        return firstElementChildGetter.call(this);
      },
      enumerable: true,
      configurable: true
    },
    lastElementChild: {
      get() {
        if (hasMountedChildren(this)) {
          return lastElementChildGetterPatched.call(this);
        }
        return lastElementChildGetter.call(this);
      },
      enumerable: true,
      configurable: true
    },
    assignedSlot: {
      get: assignedSlotGetterPatched,
      enumerable: true,
      configurable: true
    }
  });
  // IE11 extra patches for wrong prototypes
  if (hasOwnProperty.call(HTMLElement.prototype, 'innerHTML')) {
    defineProperty(HTMLElement.prototype, 'innerHTML', getOwnPropertyDescriptor(Element.prototype, 'innerHTML'));
  }
  if (hasOwnProperty.call(HTMLElement.prototype, 'outerHTML')) {
    defineProperty(HTMLElement.prototype, 'outerHTML', getOwnPropertyDescriptor(Element.prototype, 'outerHTML'));
  }
  if (hasOwnProperty.call(HTMLElement.prototype, 'children')) {
    defineProperty(HTMLElement.prototype, 'children', getOwnPropertyDescriptor(Element.prototype, 'children'));
  }
  // Deep-traversing patches from this point on:
  function querySelectorPatched() {
    const nodeList = arrayFromCollection(querySelectorAll$1.apply(this, ArraySlice.call(arguments)));
    if (isSyntheticShadowHost(this)) {
      // element with shadowRoot attached
      const owner = getNodeOwner(this);
      if (!isUndefined(getNodeKey(this))) {
        // it is a custom element, and we should then filter by slotted elements
        return getFirstSlottedMatch(this, nodeList);
      } else if (isNull(owner)) {
        return null;
      } else {
        // regular element, we should then filter by ownership
        return getFirstMatch(owner, nodeList);
      }
    } else if (isNodeShadowed(this)) {
      // element inside a shadowRoot
      const ownerKey = getNodeOwnerKey(this);
      if (!isUndefined(ownerKey)) {
        // `this` is handled by lwc, using getNodeNearestOwnerKey to include manually inserted elements in the same shadow.
        const elm = ArrayFind.call(nodeList, elm => getNodeNearestOwnerKey(elm) === ownerKey);
        return isUndefined(elm) ? null : elm;
      } else {
        // Note: we deviate from native shadow here, but are not fixing
        // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
        // `this` is a manually inserted element inside a shadowRoot, return the first element.
        return nodeList.length === 0 ? null : nodeList[0];
      }
    } else {
      if (!(this instanceof HTMLBodyElement)) {
        const elm = nodeList[0];
        return isUndefined(elm) ? null : elm;
      }
      // element belonging to the document
      const elm = ArrayFind.call(nodeList, elm => isUndefined(getNodeOwnerKey(elm)) || isGlobalPatchingSkipped(this));
      return isUndefined(elm) ? null : elm;
    }
  }
  function getFilteredArrayOfNodes(context, unfilteredNodes) {
    let filtered;
    if (isSyntheticShadowHost(context)) {
      // element with shadowRoot attached
      const owner = getNodeOwner(context);
      if (!isUndefined(getNodeKey(context))) {
        // it is a custom element, and we should then filter by slotted elements
        filtered = getAllSlottedMatches(context, unfilteredNodes);
      } else if (isNull(owner)) {
        filtered = [];
      } else {
        // regular element, we should then filter by ownership
        filtered = getAllMatches(owner, unfilteredNodes);
      }
    } else if (isNodeShadowed(context)) {
      // element inside a shadowRoot
      const ownerKey = getNodeOwnerKey(context);
      if (!isUndefined(ownerKey)) {
        // context is handled by lwc, using getNodeNearestOwnerKey to include manually inserted elements in the same shadow.
        filtered = ArrayFilter.call(unfilteredNodes, elm => getNodeNearestOwnerKey(elm) === ownerKey);
      } else {
        // Note: we deviate from native shadow here, but are not fixing
        // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
        // context is manually inserted without lwc:dom-manual, return everything
        filtered = ArraySlice.call(unfilteredNodes);
      }
    } else {
      if (context instanceof HTMLBodyElement) {
        // `context` is document.body or element belonging to the document with the patch enabled
        filtered = ArrayFilter.call(unfilteredNodes, elm => isUndefined(getNodeOwnerKey(elm)) || isGlobalPatchingSkipped(context));
      } else {
        // `context` is outside the lwc boundary and patch is not enabled.
        filtered = ArraySlice.call(unfilteredNodes);
      }
    }
    return filtered;
  }
  // The following patched methods hide shadowed elements from global
  // traversing mechanisms. They are simplified for performance reasons to
  // filter by ownership and do not account for slotted elements. This
  // compromise is fine for our synthetic shadow dom because root elements
  // cannot have slotted elements.
  // Another compromise here is that all these traversing methods will return
  // static HTMLCollection or static NodeList. We decided that this compromise
  // is not a big problem considering the amount of code that is relying on
  // the liveliness of these results are rare.
  defineProperties(Element.prototype, {
    querySelector: {
      value: querySelectorPatched,
      writable: true,
      enumerable: true,
      configurable: true
    },
    querySelectorAll: {
      value() {
        const nodeList = arrayFromCollection(querySelectorAll$1.apply(this, ArraySlice.call(arguments)));
        // Note: we deviate from native shadow here, but are not fixing
        // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
        const filteredResults = getFilteredArrayOfNodes(this, nodeList);
        return createStaticNodeList(filteredResults);
      },
      writable: true,
      enumerable: true,
      configurable: true
    }
  });
  // The following APIs are used directly by Jest internally so we avoid patching them during testing.
  {
    defineProperties(Element.prototype, {
      getElementsByClassName: {
        value() {
          const elements = arrayFromCollection(getElementsByClassName$1.apply(this, ArraySlice.call(arguments)));
          // Note: we deviate from native shadow here, but are not fixing
          // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
          return createStaticHTMLCollection(getNonPatchedFilteredArrayOfNodes(this, elements));
        },
        writable: true,
        enumerable: true,
        configurable: true
      },
      getElementsByTagName: {
        value() {
          const elements = arrayFromCollection(getElementsByTagName$1.apply(this, ArraySlice.call(arguments)));
          // Note: we deviate from native shadow here, but are not fixing
          // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
          return createStaticHTMLCollection(getNonPatchedFilteredArrayOfNodes(this, elements));
        },
        writable: true,
        enumerable: true,
        configurable: true
      },
      getElementsByTagNameNS: {
        value() {
          const elements = arrayFromCollection(getElementsByTagNameNS$1.apply(this, ArraySlice.call(arguments)));
          // Note: we deviate from native shadow here, but are not fixing
          // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
          return createStaticHTMLCollection(getNonPatchedFilteredArrayOfNodes(this, elements));
        },
        writable: true,
        enumerable: true,
        configurable: true
      }
    });
  }
  // IE11 extra patches for wrong prototypes
  if (hasOwnProperty.call(HTMLElement.prototype, 'getElementsByClassName')) {
    defineProperty(HTMLElement.prototype, 'getElementsByClassName', getOwnPropertyDescriptor(Element.prototype, 'getElementsByClassName'));
  }

  /*
   * Copyright (c) 2024, Salesforce, Inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const getRootNodePatched = _Node.prototype.getRootNode;
  assert$1.isFalse(String(getRootNodePatched).includes('[native code]'), 'Node prototype must be patched before patching focus.');
  const FocusableSelector = `
    [contenteditable],
    [tabindex],
    a[href],
    area[href],
    audio[controls],
    button,
    iframe,
    input,
    select,
    textarea,
    video[controls]
`;
  const formElementTagNames = new Set(['BUTTON', 'INPUT', 'SELECT', 'TEXTAREA']);
  function filterSequentiallyFocusableElements(elements) {
    return elements.filter(element => {
      if (hasAttribute.call(element, 'tabindex')) {
        // Even though LWC only supports tabindex values of 0 or -1,
        // passing through elements with tabindex="0" is a tighter criteria
        // than filtering out elements based on tabindex="-1".
        return getAttribute.call(element, 'tabindex') === '0';
      }
      if (formElementTagNames.has(tagNameGetter.call(element))) {
        return !hasAttribute.call(element, 'disabled');
      }
      return true;
    });
  }
  const DidAddMouseEventListeners = new WeakMap();
  // Due to browser differences, it is impossible to know what is focusable until
  // we actually try to focus it. We need to refactor our focus delegation logic
  // to verify whether or not the target was actually focused instead of trying
  // to predict focusability like we do here.
  function isVisible(element) {
    const {
      width,
      height
    } = getBoundingClientRect.call(element);
    const noZeroSize = width > 0 || height > 0;
    // The area element can be 0x0 and focusable. Hardcoding this is not ideal
    // but it will minimize changes in the current behavior.
    const isAreaElement = element.tagName === 'AREA';
    return (noZeroSize || isAreaElement) && getComputedStyle(element).visibility !== 'hidden';
  }
  // This function based on https://allyjs.io/data-tables/focusable.html
  // It won't catch everything, but should be good enough
  // There are a lot of edge cases here that we can't realistically handle
  // Determines if a particular element is tabbable, as opposed to simply focusable
  function isTabbable(element) {
    if (isSyntheticShadowHost(element) && isDelegatingFocus(element)) {
      return false;
    }
    return matches.call(element, FocusableSelector) && isVisible(element);
  }
  function hostElementFocus() {
    const _rootNode = getRootNodePatched.call(this);
    if (_rootNode === this) {
      // We invoke the focus() method even if the host is disconnected in order to eliminate
      // observable differences for component authors between synthetic and native.
      const focusable = querySelector.call(this, FocusableSelector);
      if (!isNull(focusable)) {
        // @ts-expect-error type-mismatch
        focusable.focus.apply(focusable, arguments);
      }
      return;
    }
    // If the root node is not the host element then it's either the document or a shadow root.
    const rootNode = _rootNode;
    if (rootNode.activeElement === this) {
      // The focused element should not change if the focus method is invoked
      // on the shadow-including ancestor of the currently focused element.
      return;
    }
    const focusables = arrayFromCollection(querySelectorAll$1.call(this, FocusableSelector));
    let didFocus = false;
    while (!didFocus && focusables.length !== 0) {
      const focusable = focusables.shift();
      // @ts-expect-error type-mismatch
      focusable.focus.apply(focusable, arguments);
      // Get the root node of the current focusable in case it was slotted.
      const currentRootNode = focusable.getRootNode();
      didFocus = currentRootNode.activeElement === focusable;
    }
  }
  function getTabbableSegments(host) {
    const doc = getOwnerDocument(host);
    const all = filterSequentiallyFocusableElements(arrayFromCollection(querySelectorAll.call(doc, FocusableSelector)));
    const inner = filterSequentiallyFocusableElements(arrayFromCollection(querySelectorAll$1.call(host, FocusableSelector)));
    const firstChild = inner[0];
    const lastChild = inner[inner.length - 1];
    const hostIndex = ArrayIndexOf.call(all, host);
    // Host element can show up in our "previous" section if its tabindex is 0
    // We want to filter that out here
    const firstChildIndex = hostIndex > -1 ? hostIndex : ArrayIndexOf.call(all, firstChild);
    // Account for an empty inner list
    const lastChildIndex = inner.length === 0 ? firstChildIndex + 1 : ArrayIndexOf.call(all, lastChild) + 1;
    const prev = ArraySlice.call(all, 0, firstChildIndex);
    const next = ArraySlice.call(all, lastChildIndex);
    return {
      prev,
      inner,
      next
    };
  }
  function getActiveElement(host) {
    const doc = getOwnerDocument(host);
    const activeElement = DocumentPrototypeActiveElement.call(doc);
    if (isNull(activeElement)) {
      return activeElement;
    }
    // activeElement must be child of the host and owned by it
    return (compareDocumentPosition.call(host, activeElement) & DOCUMENT_POSITION_CONTAINED_BY) !== 0 ? activeElement : null;
  }
  function relatedTargetPosition(host, relatedTarget) {
    // assert: target must be child of host
    const pos = compareDocumentPosition.call(host, relatedTarget);
    if (pos & DOCUMENT_POSITION_CONTAINED_BY) {
      // focus remains inside the host
      return 0;
    } else if (pos & DOCUMENT_POSITION_PRECEDING) {
      // focus is coming from above
      return 1;
    } else if (pos & DOCUMENT_POSITION_FOLLOWING) {
      // focus is coming from below
      return 2;
    }
    // we don't know what's going on.
    return -1;
  }
  function muteEvent(event) {
    event.preventDefault();
    event.stopPropagation();
  }
  function muteFocusEventsDuringExecution(win, func) {
    windowAddEventListener.call(win, 'focusin', muteEvent, true);
    windowAddEventListener.call(win, 'focusout', muteEvent, true);
    func();
    windowRemoveEventListener.call(win, 'focusin', muteEvent, true);
    windowRemoveEventListener.call(win, 'focusout', muteEvent, true);
  }
  function focusOnNextOrBlur(segment, target, relatedTarget) {
    const win = getOwnerWindow(relatedTarget);
    const next = getNextTabbable(segment, relatedTarget);
    if (isNull(next)) {
      // nothing to focus on, blur to invalidate the operation
      muteFocusEventsDuringExecution(win, () => {
        target.blur();
      });
    } else {
      muteFocusEventsDuringExecution(win, () => {
        next.focus();
      });
    }
  }
  let letBrowserHandleFocus = false;
  function disableKeyboardFocusNavigationRoutines() {
    letBrowserHandleFocus = true;
  }
  function enableKeyboardFocusNavigationRoutines() {
    letBrowserHandleFocus = false;
  }
  function isKeyboardFocusNavigationRoutineEnabled() {
    return !letBrowserHandleFocus;
  }
  function skipHostHandler(event) {
    if (letBrowserHandleFocus) {
      return;
    }
    const host = eventCurrentTargetGetter.call(event);
    const target = eventTargetGetter.call(event);
    // If the host delegating focus with tabindex=0 is not the target, we know
    // that the event was dispatched on a descendant node of the host. This
    // means the focus is coming from below and we don't need to do anything.
    if (host !== target) {
      // Focus is coming from above
      return;
    }
    const relatedTarget = focusEventRelatedTargetGetter.call(event);
    if (isNull(relatedTarget)) {
      // If relatedTarget is null, the user is most likely tabbing into the document from the
      // browser chrome. We could probably deduce whether focus is coming in from the top or the
      // bottom by comparing the position of the target to all tabbable elements. This is an edge
      // case and only comes up if the custom element is the first or last tabbable element in the
      // document.
      return;
    }
    const segments = getTabbableSegments(host);
    const position = relatedTargetPosition(host, relatedTarget);
    if (position === 1) {
      // Focus is coming from above
      const findTabbableElms = isTabbableFrom.bind(null, host.getRootNode());
      const first = ArrayFind.call(segments.inner, findTabbableElms);
      if (!isUndefined(first)) {
        const win = getOwnerWindow(first);
        muteFocusEventsDuringExecution(win, () => {
          first.focus();
        });
      } else {
        focusOnNextOrBlur(segments.next, target, relatedTarget);
      }
    } else if (host === target) {
      // Host is receiving focus from below, either from its shadow or from a sibling
      focusOnNextOrBlur(ArrayReverse.call(segments.prev), target, relatedTarget);
    }
  }
  function skipShadowHandler(event) {
    if (letBrowserHandleFocus) {
      return;
    }
    const relatedTarget = focusEventRelatedTargetGetter.call(event);
    if (isNull(relatedTarget)) {
      // If relatedTarget is null, the user is most likely tabbing into the document from the
      // browser chrome. We could probably deduce whether focus is coming in from the top or the
      // bottom by comparing the position of the target to all tabbable elements. This is an edge
      // case and only comes up if the custom element is the first or last tabbable element in the
      // document.
      return;
    }
    const host = eventCurrentTargetGetter.call(event);
    const segments = getTabbableSegments(host);
    if (ArrayIndexOf.call(segments.inner, relatedTarget) !== -1) {
      // If relatedTarget is contained by the host's subtree we can assume that the user is
      // tabbing between elements inside of the shadow. Do nothing.
      return;
    }
    const target = eventTargetGetter.call(event);
    // Determine where the focus is coming from (Tab or Shift+Tab)
    const position = relatedTargetPosition(host, relatedTarget);
    if (position === 1) {
      // Focus is coming from above
      focusOnNextOrBlur(segments.next, target, relatedTarget);
    }
    if (position === 2) {
      // Focus is coming from below
      focusOnNextOrBlur(ArrayReverse.call(segments.prev), target, relatedTarget);
    }
  }
  // Use this function to determine whether you can start from one root and end up
  // at another element via tabbing.
  function isTabbableFrom(fromRoot, toElm) {
    if (!isTabbable(toElm)) {
      return false;
    }
    const ownerDocument = getOwnerDocument(toElm);
    let root = toElm.getRootNode();
    while (root !== ownerDocument && root !== fromRoot) {
      const sr = root;
      const host = sr.host;
      if (getAttribute.call(host, 'tabindex') === '-1') {
        return false;
      }
      root = host && host.getRootNode();
    }
    return true;
  }
  function getNextTabbable(tabbables, relatedTarget) {
    const len = tabbables.length;
    if (len > 0) {
      for (let i = 0; i < len; i += 1) {
        const next = tabbables[i];
        if (isTabbableFrom(relatedTarget.getRootNode(), next)) {
          return next;
        }
      }
    }
    return null;
  }
  // Skips the host element
  function handleFocus(elm) {
    bindDocumentMousedownMouseupHandlers(elm);
    // Unbind any focusin listeners we may have going on
    ignoreFocusIn(elm);
    addEventListener.call(elm, 'focusin', skipHostHandler, true);
  }
  function ignoreFocus(elm) {
    removeEventListener.call(elm, 'focusin', skipHostHandler, true);
  }
  function bindDocumentMousedownMouseupHandlers(elm) {
    const ownerDocument = getOwnerDocument(elm);
    if (!DidAddMouseEventListeners.get(ownerDocument)) {
      DidAddMouseEventListeners.set(ownerDocument, true);
      addEventListener.call(ownerDocument, 'mousedown', disableKeyboardFocusNavigationRoutines, true);
      addEventListener.call(ownerDocument, 'mouseup', () => {
        // We schedule this as an async task in the mouseup handler (as
        // opposed to the mousedown handler) because we want to guarantee
        // that it will never run before the focusin handler:
        //
        // Click form element   | Click form element label
        // ==================================================
        // mousedown            | mousedown
        // FOCUSIN              | mousedown-setTimeout
        // mousedown-setTimeout | mouseup
        // mouseup              | FOCUSIN
        // mouseup-setTimeout   | mouseup-setTimeout
        setTimeout(enableKeyboardFocusNavigationRoutines);
      }, true);
      // [W-7824445] If the element is draggable, the mousedown event is dispatched before the
      // element is starting to be dragged, which disable the keyboard focus navigation routine.
      // But by specification, the mouseup event is never dispatched once the element is dropped.
      //
      // For all draggable element, we need to add an event listener to re-enable the keyboard
      // navigation routine after dragging starts.
      addEventListener.call(ownerDocument, 'dragstart', enableKeyboardFocusNavigationRoutines, true);
    }
  }
  // Skips the shadow tree
  function handleFocusIn(elm) {
    bindDocumentMousedownMouseupHandlers(elm);
    // Unbind any focus listeners we may have going on
    ignoreFocus(elm);
    // This focusin listener is to catch focusin events from keyboard interactions
    // A better solution would perhaps be to listen for keydown events, but
    // the keydown event happens on whatever element already has focus (or no element
    // at all in the case of the location bar. So, instead we have to assume that focusin
    // without a mousedown means keyboard navigation
    addEventListener.call(elm, 'focusin', skipShadowHandler, true);
  }
  function ignoreFocusIn(elm) {
    removeEventListener.call(elm, 'focusin', skipShadowHandler, true);
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const {
    blur,
    focus
  } = HTMLElement.prototype;
  /**
   * This method only applies to elements with a shadow attached to them
   */
  function tabIndexGetterPatched() {
    if (isDelegatingFocus(this) && isFalse(hasAttribute.call(this, 'tabindex'))) {
      // this covers the case where the default tabindex should be 0 because the
      // custom element is delegating its focus
      return 0;
    }
    return tabIndexGetter.call(this);
  }
  /**
   * This method only applies to elements with a shadow attached to them
   * @param value
   */
  function tabIndexSetterPatched(value) {
    // This tabIndex setter might be confusing unless it is understood that HTML
    // elements have default tabIndex property values. Natively focusable elements have
    // a default tabIndex value of 0 and all other elements have a default tabIndex
    // value of -1. For example, the tabIndex property value is -1 for both <x-foo> and
    // <x-foo tabindex="-1">, but our delegatesFocus polyfill should only kick in for
    // the latter case when the value of the tabindex attribute is -1.
    const delegatesFocus = isDelegatingFocus(this);
    // Record the state of things before invoking component setter.
    const prevValue = tabIndexGetter.call(this);
    const prevHasAttr = hasAttribute.call(this, 'tabindex');
    tabIndexSetter.call(this, value);
    // Record the state of things after invoking component setter.
    const currValue = tabIndexGetter.call(this);
    const currHasAttr = hasAttribute.call(this, 'tabindex');
    const didValueChange = prevValue !== currValue;
    // If the tabindex attribute is initially rendered, we can assume that this setter has
    // previously executed and a listener has been added. We must remove that listener if
    // the tabIndex property value has changed or if the component no longer renders a
    // tabindex attribute.
    if (prevHasAttr && (didValueChange || isFalse(currHasAttr))) {
      if (prevValue === -1) {
        ignoreFocusIn(this);
      }
      if (prevValue === 0 && delegatesFocus) {
        ignoreFocus(this);
      }
    }
    // If a tabindex attribute was not rendered after invoking its setter, it means the
    // component is taking control. Do nothing.
    if (isFalse(currHasAttr)) {
      return;
    }
    // If the tabindex attribute is initially rendered, we can assume that this setter has
    // previously executed and a listener has been added. If the tabindex attribute is still
    // rendered after invoking the setter AND the tabIndex property value has not changed,
    // we don't need to do any work.
    if (prevHasAttr && currHasAttr && isFalse(didValueChange)) {
      return;
    }
    // At this point we know that a tabindex attribute was rendered after invoking the
    // setter and that either:
    // 1) This is the first time this setter is being invoked.
    // 2) This is not the first time this setter is being invoked and the value is changing.
    // We need to add the appropriate listeners in either case.
    if (currValue === -1) {
      // Add the magic to skip the shadow tree
      handleFocusIn(this);
    }
    if (currValue === 0 && delegatesFocus) {
      // Add the magic to skip the host element
      handleFocus(this);
    }
  }
  /**
   * This method only applies to elements with a shadow attached to them
   */
  function blurPatched() {
    if (isDelegatingFocus(this)) {
      const currentActiveElement = getActiveElement(this);
      if (!isNull(currentActiveElement)) {
        // if there is an active element, blur it (intentionally using the dot notation in case the user defines the blur routine)
        currentActiveElement.blur();
        return;
      }
    }
    return blur.call(this);
  }
  function focusPatched() {
    // Save enabled state
    const originallyEnabled = isKeyboardFocusNavigationRoutineEnabled();
    // Change state by disabling if originally enabled
    if (originallyEnabled) {
      disableKeyboardFocusNavigationRoutines();
    }
    if (isSyntheticShadowHost(this) && isDelegatingFocus(this)) {
      hostElementFocus.call(this);
      return;
    }
    // Typescript does not like it when you treat the `arguments` object as an array
    // @ts-expect-error type-mismatch
    focus.apply(this, arguments);
    // Restore state by enabling if originally enabled
    if (originallyEnabled) {
      enableKeyboardFocusNavigationRoutines();
    }
  }
  // Non-deep-traversing patches: this descriptor map includes all descriptors that
  // do not five access to nodes beyond the immediate children.
  defineProperties(HTMLElement.prototype, {
    tabIndex: {
      get() {
        if (isSyntheticShadowHost(this)) {
          return tabIndexGetterPatched.call(this);
        }
        return tabIndexGetter.call(this);
      },
      set(v) {
        if (isSyntheticShadowHost(this)) {
          return tabIndexSetterPatched.call(this, v);
        }
        return tabIndexSetter.call(this, v);
      },
      enumerable: true,
      configurable: true
    },
    blur: {
      value() {
        if (isSyntheticShadowHost(this)) {
          return blurPatched.call(this);
        }
        blur.call(this);
      },
      enumerable: true,
      writable: true,
      configurable: true
    },
    focus: {
      value() {
        // Typescript does not like it when you treat the `arguments` object as an array
        // @ts-expect-error type-mismatch
        focusPatched.apply(this, arguments);
      },
      enumerable: true,
      writable: true,
      configurable: true
    }
  });
  // Note: In JSDOM innerText is not implemented: https://github.com/jsdom/jsdom/issues/1245
  if (innerTextGetter !== null && innerTextSetter !== null) {
    defineProperty(HTMLElement.prototype, 'innerText', {
      get() {
        // Note: we deviate from native shadow here, but are not fixing
        // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
        return innerTextGetter.call(this);
      },
      set(v) {
        innerTextSetter.call(this, v);
      },
      enumerable: true,
      configurable: true
    });
  }
  // Note: Firefox does not have outerText, https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/outerText
  if (outerTextGetter !== null && outerTextSetter !== null) {
    // From https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/outerText :
    // HTMLElement.outerText is a non-standard property. As a getter, it returns the same value as Node.innerText.
    // As a setter, it removes the current node and replaces it with the given text.
    defineProperty(HTMLElement.prototype, 'outerText', {
      get() {
        // Note: we deviate from native shadow here, but are not fixing
        // due to backwards compat: https://github.com/salesforce/lwc/pull/3103
        return outerTextGetter.call(this);
      },
      set(v) {
        // Invoking the `outerText` setter on a host element should trigger its disconnection, but until we merge node reactions, it will not work.
        // We could reimplement the outerText setter in JavaScript ([blink implementation](https://source.chromium.org/chromium/chromium/src/+/master:third_party/blink/renderer/core/html/html_element.cc;l=841-879;drc=6e8b402a6231405b753919029c9027404325ea00;bpv=0;bpt=1))
        // but the benefits don't worth the efforts.
        outerTextSetter.call(this, v);
      },
      enumerable: true,
      configurable: true
    });
  }

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  function getShadowToken(node) {
    return node[KEY__SHADOW_TOKEN];
  }
  function setShadowToken(node, shadowToken) {
    node[KEY__SHADOW_TOKEN] = shadowToken;
  }
  /**
   * Patching Element.prototype.$shadowToken$ to mark elements a portal:
   * - we use a property to allow engines to set a custom attribute that should be
   * placed into the element to sandbox the css rules defined for the template.
   * - this custom attribute must be unique.
   */
  defineProperty(Element.prototype, KEY__SHADOW_TOKEN, {
    set(shadowToken) {
      const oldShadowToken = this[KEY__SHADOW_TOKEN_PRIVATE];
      if (!isUndefined(oldShadowToken) && oldShadowToken !== shadowToken) {
        removeAttribute.call(this, oldShadowToken);
      }
      if (!isUndefined(shadowToken)) {
        setAttribute.call(this, shadowToken, '');
      }
      this[KEY__SHADOW_TOKEN_PRIVATE] = shadowToken;
    },
    get() {
      return this[KEY__SHADOW_TOKEN_PRIVATE];
    },
    configurable: true
  });
  function recursivelySetShadowResolver(node, fn) {
    node[KEY__SHADOW_RESOLVER] = fn;
    // Recurse using firstChild/nextSibling because browsers use a linked list under the hood to
    // represent the DOM, so childNodes/children would cause an unnecessary array allocation.
    // https://viethung.space/blog/2020/09/01/Browser-from-Scratch-DOM-API/#Choosing-DOM-tree-data-structure
    let child = firstChildGetter.call(node);
    while (!isNull(child)) {
      recursivelySetShadowResolver(child, fn);
      child = nextSiblingGetter.call(child);
    }
  }
  defineProperty(Element.prototype, KEY__SHADOW_STATIC, {
    set(v) {
      // Marking an element as static will propagate the shadow resolver to the children.
      if (v) {
        const fn = this[KEY__SHADOW_RESOLVER];
        recursivelySetShadowResolver(this, fn);
      }
      this[KEY__SHADOW_STATIC_PRIVATE] = v;
    },
    get() {
      return this[KEY__SHADOW_STATIC_PRIVATE];
    },
    configurable: true
  });

  /*
   * Copyright (c) 2023, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  // TODO [#3733]: remove this entire file when we can remove legacy scope tokens
  function getLegacyShadowToken(node) {
    return node[KEY__LEGACY_SHADOW_TOKEN];
  }
  function setLegacyShadowToken(node, shadowToken) {
    node[KEY__LEGACY_SHADOW_TOKEN] = shadowToken;
  }
  /**
   * Patching Element.prototype.$legacyShadowToken$ to mark elements a portal:
   * Same as $shadowToken$ but for legacy CSS scope tokens.
   */
  defineProperty(Element.prototype, KEY__LEGACY_SHADOW_TOKEN, {
    set(shadowToken) {
      const oldShadowToken = this[KEY__LEGACY_SHADOW_TOKEN_PRIVATE];
      if (!isUndefined(oldShadowToken) && oldShadowToken !== shadowToken) {
        removeAttribute.call(this, oldShadowToken);
      }
      if (!isUndefined(shadowToken)) {
        setAttribute.call(this, shadowToken, '');
      }
      this[KEY__LEGACY_SHADOW_TOKEN_PRIVATE] = shadowToken;
    },
    get() {
      return this[KEY__LEGACY_SHADOW_TOKEN_PRIVATE];
    },
    configurable: true
  });

  /*
   * Copyright (c) 2018, salesforce.com, inc.
   * All rights reserved.
   * SPDX-License-Identifier: MIT
   * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
   */
  const DomManualPrivateKey = '$$DomManualKey$$';
  // Resolver function used when a node is removed from within a portal
  const DocumentResolverFn = function () {};
  // We can use a single observer without having to worry about leaking because
  // "Registered observers in a node’s registered observer list have a weak
  // reference to the node."
  // https://dom.spec.whatwg.org/#garbage-collection
  let portalObserver;
  const portalObserverConfig = {
    childList: true
  };
  // TODO [#3733]: remove support for legacy scope tokens
  function adoptChildNode(node, fn, shadowToken, legacyShadowToken) {
    const previousNodeShadowResolver = getShadowRootResolver(node);
    if (previousNodeShadowResolver === fn) {
      return; // nothing to do here, it is already correctly patched
    }
    setShadowRootResolver(node, fn);
    if (node instanceof Element) {
      setShadowToken(node, shadowToken);
      if (lwcRuntimeFlags.ENABLE_LEGACY_SCOPE_TOKENS) {
        setLegacyShadowToken(node, legacyShadowToken);
      }
      if (isSyntheticShadowHost(node)) {
        // Root LWC elements can't get content slotted into them, therefore we don't observe their children.
        return;
      }
      if (isUndefined(previousNodeShadowResolver)) {
        // we only care about Element without shadowResolver (no MO.observe has been called)
        MutationObserverObserve.call(portalObserver, node, portalObserverConfig);
      }
      // recursively patching all children as well
      const childNodes = childNodesGetter.call(node);
      for (let i = 0, len = childNodes.length; i < len; i += 1) {
        adoptChildNode(childNodes[i], fn, shadowToken, legacyShadowToken);
      }
    }
  }
  function initPortalObserver() {
    return new MO(mutations => {
      forEach.call(mutations, mutation => {
        /**
         * This routine will process all nodes added or removed from elm (which is marked as a portal)
         * When adding a node to the portal element, we should add the ownership.
         * When removing a node from the portal element, this ownership should be removed.
         *
         * There is some special cases in which MutationObserver may call with stacked mutations (the same node
         * will be in addedNodes and removedNodes) or with false positives (a node that is removed and re-appended
         * in the same tick) for those cases, we cover by checking that the node is contained
         * (or not in the case of removal) by the element.
         */
        const {
          target: elm,
          addedNodes,
          removedNodes
        } = mutation;
        // the target of the mutation should always have a ShadowRootResolver attached to it
        const fn = getShadowRootResolver(elm);
        const shadowToken = getShadowToken(elm);
        const legacyShadowToken = lwcRuntimeFlags.ENABLE_LEGACY_SCOPE_TOKENS ? getLegacyShadowToken(elm) : undefined;
        // Process removals first to handle the case where an element is removed and reinserted
        for (let i = 0, len = removedNodes.length; i < len; i += 1) {
          const node = removedNodes[i];
          if (!(compareDocumentPosition.call(elm, node) & _Node.DOCUMENT_POSITION_CONTAINED_BY)) {
            adoptChildNode(node, DocumentResolverFn, undefined, undefined);
          }
        }
        for (let i = 0, len = addedNodes.length; i < len; i += 1) {
          const node = addedNodes[i];
          if (compareDocumentPosition.call(elm, node) & _Node.DOCUMENT_POSITION_CONTAINED_BY) {
            adoptChildNode(node, fn, shadowToken, legacyShadowToken);
          }
        }
      });
    });
  }
  function markElementAsPortal(elm) {
    if (isUndefined(portalObserver)) {
      portalObserver = initPortalObserver();
    }
    if (isUndefined(getShadowRootResolver(elm))) {
      // only an element from a within a shadowRoot should be used here
      throw new Error(`Invalid Element`);
    }
    // install mutation observer for portals
    MutationObserverObserve.call(portalObserver, elm, portalObserverConfig);
    // TODO [#1253]: optimization to synchronously adopt new child nodes added
    // to this elm, we can do that by patching the most common operations
    // on the node itself
  }
  /**
   * Patching Element.prototype.$domManual$ to mark elements as portal:
   * - we use a property to allow engines to signal that a particular element in
   * a shadow supports manual insertion of child nodes.
   * - this signal comes as a boolean value, and we use it to install the MO instance
   * onto the element, to propagate the $ownerKey$ and $shadowToken$ to all new
   * child nodes.
   * - at the moment, there is no way to undo this operation, once the element is
   * marked as $domManual$, setting it to false does nothing.
   */
  // TODO [#1306]: rename this to $observerConnection$
  defineProperty(Element.prototype, '$domManual$', {
    set(v) {
      this[DomManualPrivateKey] = v;
      if (isTrue(v)) {
        markElementAsPortal(this);
      }
    },
    get() {
      return this[DomManualPrivateKey];
    },
    configurable: true
  });
  /** version: 9.2.2 */
}

/**
 * Copyright (c) 2026 Salesforce, Inc.
 */
/**
 * Copyright (c) 2026 Salesforce, Inc.
 */
/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
/**
 *
 * @param value
 * @param msg
 */
function invariant(value, msg) {
  if (!value) {
    throw new Error(`Invariant Violation: ${msg}`);
  }
}
/**
 *
 * @param value
 * @param msg
 */
function isTrue$1(value, msg) {
  if (!value) {
    throw new Error(`Assert Violation: ${msg}`);
  }
}
/**
 *
 * @param value
 * @param msg
 */
function isFalse$1(value, msg) {
  if (value) {
    throw new Error(`Assert Violation: ${msg}`);
  }
}
/**
 *
 * @param msg
 */
function fail(msg) {
  throw new Error(msg);
}
var assert = /*#__PURE__*/Object.freeze({
  __proto__: null,
  fail: fail,
  invariant: invariant,
  isFalse: isFalse$1,
  isTrue: isTrue$1
});

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const {
  /** Detached {@linkcode Object.assign}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/assign MDN Reference}. */
  assign,
  /** Detached {@linkcode Object.create}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create MDN Reference}. */
  create,
  /** Detached {@linkcode Object.defineProperties}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperties MDN Reference}. */
  defineProperties,
  /** Detached {@linkcode Object.defineProperty}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperty MDN Reference}. */
  defineProperty,
  /** Detached {@linkcode Object.entries}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/entries MDN Reference}. */
  entries,
  /** Detached {@linkcode Object.freeze}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/freeze MDN Reference}. */
  freeze,
  /** Detached {@linkcode Object.getOwnPropertyDescriptor}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/getOwnPropertyDescriptor MDN Reference}. */
  getOwnPropertyDescriptor: getOwnPropertyDescriptor$1,
  /** Detached {@linkcode Object.getOwnPropertyDescriptors}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/getOwnPropertyDescriptors MDN Reference}. */
  getOwnPropertyDescriptors,
  /** Detached {@linkcode Object.getOwnPropertyNames}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/getOwnPropertyNames MDN Reference}. */
  getOwnPropertyNames: getOwnPropertyNames$1,
  /** Detached {@linkcode Object.getPrototypeOf}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/getPrototypeOf MDN Reference}. */
  getPrototypeOf: getPrototypeOf$1,
  /** Detached {@linkcode Object.hasOwnProperty}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/hasOwnProperty MDN Reference}. */
  hasOwnProperty: hasOwnProperty$1,
  /** Detached {@linkcode Object.isFrozen}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/isFrozen MDN Reference}. */
  isFrozen,
  /** Detached {@linkcode Object.keys}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/keys MDN Reference}. */
  keys,
  /** Detached {@linkcode Object.seal}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/seal MDN Reference}. */
  seal,
  /** Detached {@linkcode Object.setPrototypeOf}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/setPrototypeOf MDN Reference}. */
  setPrototypeOf
} = Object;
const {
  /** Detached {@linkcode Array.isArray}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/isArray MDN Reference}. */
  isArray: isArray$1,
  /** Detached {@linkcode Array.from}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/from MDN Reference}. */
  from: ArrayFrom
} = Array;
// For some reason, JSDoc don't get picked up for multiple renamed destructured constants (even
// though it works fine for one, e.g. isArray), so comments for these are added to the export
// statement, rather than this declaration.
const {
  filter: ArrayFilter,
  indexOf: ArrayIndexOf,
  join: ArrayJoin,
  map: ArrayMap,
  pop: ArrayPop,
  push: ArrayPush$1,
  slice: ArraySlice,
  splice: ArraySplice,
  unshift: ArrayUnshift,
  forEach // Weird anomaly!
} = Array.prototype;
/** Detached {@linkcode String.fromCharCode}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/fromCharCode MDN Reference}. */
const {
  fromCharCode: StringFromCharCode
} = String;
// No JSDocs here - see comment for Array.prototype
const {
  charAt: StringCharAt,
  charCodeAt: StringCharCodeAt,
  replace: StringReplace,
  slice: StringSlice,
  toLowerCase: StringToLowerCase,
  trim: StringTrim
} = String.prototype;
/**
 * Determines whether the argument is `undefined`.
 * @param obj Value to test
 * @returns `true` if the value is `undefined`.
 */
function isUndefined$1(obj) {
  return obj === undefined;
}
/**
 * Determines whether the argument is `null`.
 * @param obj Value to test
 * @returns `true` if the value is `null`.
 */
function isNull(obj) {
  return obj === null;
}
/**
 * Determines whether the argument is `true`.
 * @param obj Value to test
 * @returns `true` if the value is `true`.
 */
function isTrue(obj) {
  return obj === true;
}
/**
 * Determines whether the argument is `false`.
 * @param obj Value to test
 * @returns `true` if the value is `false`.
 */
function isFalse(obj) {
  return obj === false;
}
/**
 * Determines whether the argument is a function.
 * @param obj Value to test
 * @returns `true` if the value is a function.
 */
// Replacing `Function` with a narrower type that works for all our use cases is tricky...
// eslint-disable-next-line @typescript-eslint/no-unsafe-function-type
function isFunction$1(obj) {
  return typeof obj === 'function';
}
/**
 * Determines whether the argument is an object or null.
 * @param obj Value to test
 * @returns `true` if the value is an object or null.
 */
function isObject(obj) {
  return typeof obj === 'object';
}
/**
 * Determines whether the argument is a string.
 * @param obj Value to test
 * @returns `true` if the value is a string.
 */
function isString(obj) {
  return typeof obj === 'string';
}
/** Does nothing! 🚀 */
function noop() {
  /* Do nothing */
}
const OtS = {}.toString;
/**
 * Converts the argument to a string, safely accounting for objects with "null" prototype.
 * Note that `toString(null)` returns `"[object Null]"` rather than `"null"`.
 * @param obj Value to convert to a string.
 * @returns String representation of the value.
 */
function toString(obj) {
  if (obj?.toString) {
    // Arrays might hold objects with "null" prototype So using
    // Array.prototype.toString directly will cause an error Iterate through
    // all the items and handle individually.
    if (isArray$1(obj)) {
      // This behavior is slightly different from Array#toString:
      // 1. Array#toString calls `this.join`, rather than Array#join
      // Ex: arr = []; arr.join = () => 1; arr.toString() === 1; toString(arr) === ''
      // 2. Array#toString delegates to Object#toString if `this.join` is not a function
      // Ex: arr = []; arr.join = 'no'; arr.toString() === '[object Array]; toString(arr) = ''
      // 3. Array#toString converts null/undefined to ''
      // Ex: arr = [null, undefined]; arr.toString() === ','; toString(arr) === '[object Null],undefined'
      // 4. Array#toString converts recursive references to arrays to ''
      // Ex: arr = [1]; arr.push(arr, 2); arr.toString() === '1,,2'; toString(arr) throws
      // Ref: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/toString
      return ArrayJoin.call(ArrayMap.call(obj, toString), ',');
    }
    return obj.toString();
  } else if (typeof obj === 'object') {
    // This catches null and returns "[object Null]". Weird, but kept for backwards compatibility.
    return OtS.call(obj);
  } else {
    return String(obj);
  }
}
/**
 * Gets the property descriptor for the given object and property key. Similar to
 * {@linkcode Object.getOwnPropertyDescriptor}, but looks up the prototype chain.
 * @param o Value to get the property descriptor for
 * @param p Property key to get the descriptor for
 * @returns The property descriptor for the given object and property key.
 */
function getPropertyDescriptor(o, p) {
  do {
    const d = getOwnPropertyDescriptor$1(o, p);
    if (!isUndefined$1(d)) {
      return d;
    }
    o = getPrototypeOf$1(o);
  } while (o !== null);
}

/*
 * Copyright (c) 2023, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
// These must be updated when the enum is updated.
// It's a bit annoying to do have to do this manually, but this makes the file tree-shakeable,
// passing the `verify-treeshakeable.js` test.
const allVersions = [58 /* APIVersion.V58_244_SUMMER_23 */, 59 /* APIVersion.V59_246_WINTER_24 */, 60 /* APIVersion.V60_248_SPRING_24 */, 61 /* APIVersion.V61_250_SUMMER_24 */, 62 /* APIVersion.V62_252_WINTER_25 */, 63 /* APIVersion.V63_254_SPRING_25 */, 64 /* APIVersion.V64_256_SUMMER_25 */, 65 /* APIVersion.V65_258_WINTER_26 */, 66 /* APIVersion.V66_260_SPRING_26 */];
const LOWEST_API_VERSION = allVersions[0];
/**
 * @param apiVersionFeature
 */
function minApiVersion(apiVersionFeature) {
  switch (apiVersionFeature) {
    case 0 /* APIFeature.LOWERCASE_SCOPE_TOKENS */:
    case 1 /* APIFeature.TREAT_ALL_PARSE5_ERRORS_AS_ERRORS */:
      return 59 /* APIVersion.V59_246_WINTER_24 */;
    case 3 /* APIFeature.DISABLE_OBJECT_REST_SPREAD_TRANSFORMATION */:
    case 4 /* APIFeature.SKIP_UNNECESSARY_REGISTER_DECORATORS */:
    case 5 /* APIFeature.USE_COMMENTS_FOR_FRAGMENT_BOOKENDS */:
    case 2 /* APIFeature.USE_FRAGMENTS_FOR_LIGHT_DOM_SLOTS */:
      return 60 /* APIVersion.V60_248_SPRING_24 */;
    case 7 /* APIFeature.ENABLE_ELEMENT_INTERNALS_AND_FACE */:
    case 6 /* APIFeature.USE_LIGHT_DOM_SLOT_FORWARDING */:
      return 61 /* APIVersion.V61_250_SUMMER_24 */;
    case 8 /* APIFeature.ENABLE_THIS_DOT_HOST_ELEMENT */:
    case 9 /* APIFeature.ENABLE_THIS_DOT_STYLE */:
    case 10 /* APIFeature.TEMPLATE_CLASS_NAME_OBJECT_BINDING */:
      return 62 /* APIVersion.V62_252_WINTER_25 */;
    case 11 /* APIFeature.ENABLE_COMPLEX_TEMPLATE_EXPRESSIONS */:
      return 66 /* APIVersion.V66_260_SPRING_26 */;
  }
}
/**
 *
 * @param apiVersionFeature
 * @param apiVersion
 */
function isAPIFeatureEnabled(apiVersionFeature, apiVersion) {
  return apiVersion >= minApiVersion(apiVersionFeature);
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
/**
 * According to the following list, there are 48 aria attributes of which two (ariaDropEffect and
 * ariaGrabbed) are deprecated:
 * https://www.w3.org/TR/wai-aria-1.1/#x6-6-definitions-of-states-and-properties-all-aria-attributes
 *
 * The above list of 46 aria attributes is consistent with the following resources:
 * https://github.com/w3c/aria/pull/708/files#diff-eacf331f0ffc35d4b482f1d15a887d3bR11060
 * https://wicg.github.io/aom/spec/aria-reflection.html
 *
 * NOTE: If you update this list, please update test files that implicitly reference this list!
 * Searching the codebase for `aria-flowto` and `ariaFlowTo` should be good enough to find all usages.
 */
const AriaPropertyNames = ['ariaActiveDescendant', 'ariaAtomic', 'ariaAutoComplete', 'ariaBusy', 'ariaChecked', 'ariaColCount', 'ariaColIndex', 'ariaColIndexText', 'ariaColSpan', 'ariaControls', 'ariaCurrent', 'ariaDescribedBy', 'ariaDescription', 'ariaDetails', 'ariaDisabled', 'ariaErrorMessage', 'ariaExpanded', 'ariaFlowTo', 'ariaHasPopup', 'ariaHidden', 'ariaInvalid', 'ariaKeyShortcuts', 'ariaLabel', 'ariaLabelledBy', 'ariaLevel', 'ariaLive', 'ariaModal', 'ariaMultiLine', 'ariaMultiSelectable', 'ariaOrientation', 'ariaOwns', 'ariaPlaceholder', 'ariaPosInSet', 'ariaPressed', 'ariaReadOnly', 'ariaRelevant', 'ariaRequired', 'ariaRoleDescription', 'ariaRowCount', 'ariaRowIndex', 'ariaRowIndexText', 'ariaRowSpan', 'ariaSelected', 'ariaSetSize', 'ariaSort', 'ariaValueMax', 'ariaValueMin', 'ariaValueNow', 'ariaValueText', 'ariaBrailleLabel', 'ariaBrailleRoleDescription', 'role'];
const {
  AriaAttrNameToPropNameMap,
  AriaPropNameToAttrNameMap
} = /*@__PURE__*/(() => {
  const AriaAttrNameToPropNameMap = create(null);
  const AriaPropNameToAttrNameMap = create(null);
  // Synthetic creation of all AOM property descriptors for Custom Elements
  forEach.call(AriaPropertyNames, propName => {
    const attrName = StringToLowerCase.call(StringReplace.call(propName, /^aria/, () => 'aria-'));
    // These type assertions are because the map types are a 1:1 mapping of ariaX to aria-x.
    // TypeScript knows we have one of ariaX | ariaY and one of aria-x | aria-y, and tries to
    // prevent us from doing ariaX: aria-y, but we that it's safe.
    AriaAttrNameToPropNameMap[attrName] = propName;
    AriaPropNameToAttrNameMap[propName] = attrName;
  });
  return {
    AriaAttrNameToPropNameMap,
    AriaPropNameToAttrNameMap
  };
})();

/*
 * Copyright (c) 2024, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const ContextEventName = 'lightning:context-request';
let contextKeys;
function getContextKeys() {
  return contextKeys;
}
function isTrustedContext(target) {
  {
    // The runtime didn't set a trustedContext set
    // this check should only be performed for runtimes that care about filtering context participants to track
    return true;
  }
}

/*
 * Copyright (c) 2023, Salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const KEY__SHADOW_RESOLVER = '$shadowResolver$';
const KEY__SHADOW_STATIC = '$shadowStaticNode$';
const KEY__SHADOW_TOKEN = '$shadowToken$';
const KEY__SYNTHETIC_MODE = '$$lwc-synthetic-mode';
const KEY__SCOPED_CSS = '$scoped$';
const KEY__NATIVE_ONLY_CSS = '$nativeOnly$';
const KEY__NATIVE_GET_ELEMENT_BY_ID = '$nativeGetElementById$';
const KEY__NATIVE_QUERY_SELECTOR_ALL = '$nativeQuerySelectorAll$';
const XML_NAMESPACE = 'http://www.w3.org/XML/1998/namespace';
const SVG_NAMESPACE = 'http://www.w3.org/2000/svg';
const XLINK_NAMESPACE = 'http://www.w3.org/1999/xlink';

/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const CAMEL_REGEX = /-([a-z])/g;
// These are HTML standard prop/attribute IDL mappings, but are not predictable based on camel/kebab-case conversion
const SPECIAL_PROPERTY_ATTRIBUTE_MAPPING = /*@__PURE__@*/new Map([['accessKey', 'accesskey'], ['readOnly', 'readonly'], ['tabIndex', 'tabindex'], ['bgColor', 'bgcolor'], ['colSpan', 'colspan'], ['rowSpan', 'rowspan'], ['contentEditable', 'contenteditable'], ['crossOrigin', 'crossorigin'], ['dateTime', 'datetime'], ['formAction', 'formaction'], ['isMap', 'ismap'], ['maxLength', 'maxlength'], ['minLength', 'minlength'], ['noValidate', 'novalidate'], ['useMap', 'usemap'], ['htmlFor', 'for']]);
// Global properties that this framework currently reflects. For CSR, the native
// descriptors for these properties are added from HTMLElement.prototype to
// LightningElement.prototype. For SSR, in order to match CSR behavior, this
// list is used to determine which attributes to reflect.
const REFLECTIVE_GLOBAL_PROPERTY_SET = /*@__PURE__@*/new Set(['accessKey', 'dir', 'draggable', 'hidden', 'id', 'lang', 'spellcheck', 'tabIndex', 'title']);
/**
 * Map associating previously transformed HTML property into HTML attribute.
 */
const CACHED_PROPERTY_ATTRIBUTE_MAPPING = /*@__PURE__@*/new Map();
/**
 *
 * @param propName
 */
function htmlPropertyToAttribute(propName) {
  const ariaAttributeName = AriaPropNameToAttrNameMap[propName];
  if (!isUndefined$1(ariaAttributeName)) {
    return ariaAttributeName;
  }
  const specialAttributeName = SPECIAL_PROPERTY_ATTRIBUTE_MAPPING.get(propName);
  if (!isUndefined$1(specialAttributeName)) {
    return specialAttributeName;
  }
  const cachedAttributeName = CACHED_PROPERTY_ATTRIBUTE_MAPPING.get(propName);
  if (!isUndefined$1(cachedAttributeName)) {
    return cachedAttributeName;
  }
  let attributeName = '';
  for (let i = 0, len = propName.length; i < len; i++) {
    const code = StringCharCodeAt.call(propName, i);
    if (code >= 65 &&
    // "A"
    code <= 90 // "Z"
    ) {
      attributeName += '-' + StringFromCharCode(code + 32);
    } else {
      attributeName += StringFromCharCode(code);
    }
  }
  CACHED_PROPERTY_ATTRIBUTE_MAPPING.set(propName, attributeName);
  return attributeName;
}
/**
 * Map associating previously transformed kabab-case attributes into camel-case props.
 */
const CACHED_KEBAB_CAMEL_MAPPING = /*@__PURE__@*/new Map();
/**
 *
 * @param attrName
 */
function kebabCaseToCamelCase(attrName) {
  let result = CACHED_KEBAB_CAMEL_MAPPING.get(attrName);
  if (isUndefined$1(result)) {
    result = StringReplace.call(attrName, CAMEL_REGEX, g => g[1].toUpperCase());
    CACHED_KEBAB_CAMEL_MAPPING.set(attrName, result);
  }
  return result;
}

/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
/**
 * [ncls] - Normalize class name attribute.
 *
 * Transforms the provided class property value from an object/string into a string the diffing algo
 * can operate on.
 *
 * This implementation is borrowed from Vue:
 * https://github.com/vuejs/core/blob/e790e1bdd7df7be39e14780529db86e4da47a3db/packages/shared/src/normalizeProp.ts#L63-L82
 */
function normalizeClass(value) {
  if (isUndefined$1(value) || isNull(value)) {
    // Returning undefined here improves initial render cost, because the old vnode's class will be considered
    // undefined in the `patchClassAttribute` routine, so `oldClass === newClass` will be true so we return early
    return undefined;
  }
  let res = '';
  if (isString(value)) {
    res = value;
  } else if (isArray$1(value)) {
    for (let i = 0; i < value.length; i++) {
      const normalized = normalizeClass(value[i]);
      if (normalized) {
        res += normalized + ' ';
      }
    }
  } else if (isObject(value) && !isNull(value)) {
    // Iterate own enumerable keys of the object
    const _keys = keys(value);
    for (let i = 0; i < _keys.length; i += 1) {
      const key = _keys[i];
      if (value[key]) {
        res += key + ' ';
      }
    }
  }
  return StringTrim.call(res);
}
let sanitizeHtmlContentImpl = () => {
  // locker-service patches this function during runtime to sanitize HTML content.
  throw new Error('sanitizeHtmlContent hook must be implemented.');
};
/**
 * EXPERIMENTAL: This function acts like a hook for Lightning Locker Service and other similar
 * libraries to sanitize HTML content. This hook process the content passed via the template to
 * lwc:inner-html directive.
 * It is meant to be overridden via `setHooks`; it throws an error by default.
 */
const sanitizeHtmlContent = value => {
  return sanitizeHtmlContentImpl();
};
function flattenStylesheets(stylesheets) {
  const list = [];
  for (const stylesheet of stylesheets) {
    if (!isArray$1(stylesheet)) {
      list.push(stylesheet);
    } else {
      list.push(...flattenStylesheets(stylesheet));
    }
  }
  return list;
}
function isTrustedSignal(target) {
  {
    return false;
  }
}
if (!globalThis.lwcRuntimeFlags) {
  Object.defineProperty(globalThis, 'lwcRuntimeFlags', {
    value: create(null)
  });
}
/**
 * Whether reporting is enabled.
 *
 * Note that this may seem redundant, given you can just check if the currentDispatcher is undefined,
 * but it turns out that Terser only strips out unused code if we use this explicit boolean.
 */
let enabled$1 = false;
/**
 * Return true if reporting is enabled
 */
function isReportingEnabled() {
  return enabled$1;
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
function getComponentTag(vm) {
  return `<${StringToLowerCase.call(vm.tagName)}>`;
}
// TODO [#1695]: Unify getComponentStack and getErrorComponentStack
function getComponentStack(vm) {
  const stack = [];
  let prefix = '';
  while (!isNull(vm.owner)) {
    ArrayPush$1.call(stack, prefix + getComponentTag(vm));
    vm = vm.owner;
    prefix += '\t';
  }
  return ArrayJoin.call(stack, '\n');
}
function getErrorComponentStack(vm) {
  const wcStack = [];
  let currentVm = vm;
  while (!isNull(currentVm)) {
    ArrayPush$1.call(wcStack, getComponentTag(currentVm));
    currentVm = currentVm.owner;
  }
  return wcStack.reverse().join('\n\t');
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
function addErrorComponentStack(vm, error) {
  if (!isFrozen(error) && isUndefined$1(error.wcStack)) {
    const wcStack = getErrorComponentStack(vm);
    defineProperty(error, 'wcStack', {
      get() {
        return wcStack;
      }
    });
  }
}

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const alreadyLoggedMessages = new Set();
function log(method, message, vm, once) {
  let msg = `[LWC ${method}]: ${message}`;
  if (!isUndefined$1(vm)) {
    msg = `${msg}\n${getComponentStack(vm)}`;
  }
  if (once) {
    if (alreadyLoggedMessages.has(msg)) {
      return;
    }
    alreadyLoggedMessages.add(msg);
  }
  try {
    throw new Error(msg);
  } catch (e) {
    /* eslint-disable-next-line no-console */
    console[method](e);
  }
}
function logError(message, vm) {
  log('error', message, vm, false);
}
function logWarnOnce(message, vm) {
  log('warn', message, vm, true);
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
let nextTickCallbackQueue = [];
const SPACE_CHAR = 32;
const EmptyObject = seal(create(null));
const EmptyArray = seal([]);
function flushCallbackQueue() {
  const callbacks = nextTickCallbackQueue;
  nextTickCallbackQueue = []; // reset to a new queue
  for (let i = 0, len = callbacks.length; i < len; i += 1) {
    callbacks[i]();
  }
}
function addCallbackToNextTick(callback) {
  if (nextTickCallbackQueue.length === 0) {
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    Promise.resolve().then(flushCallbackQueue);
  }
  ArrayPush$1.call(nextTickCallbackQueue, callback);
}
// Throw an error if we're running in prod mode. Ensures code is truly removed from prod mode.
function assertNotProd() {
  /* istanbul ignore if */
  {
    // this method should never leak to prod
    throw new ReferenceError();
  }
}
function shouldBeFormAssociated(Ctor) {
  const ctorFormAssociated = Boolean(Ctor.formAssociated);
  const apiVersion = getComponentAPIVersion(Ctor);
  const apiFeatureEnabled = isAPIFeatureEnabled(7 /* APIFeature.ENABLE_ELEMENT_INTERNALS_AND_FACE */, apiVersion);
  return ctorFormAssociated && apiFeatureEnabled;
}

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const TargetToReactiveRecordMap = new WeakMap();
function getReactiveRecord(target) {
  let reactiveRecord = TargetToReactiveRecordMap.get(target);
  if (isUndefined$1(reactiveRecord)) {
    const newRecord = create(null);
    reactiveRecord = newRecord;
    TargetToReactiveRecordMap.set(target, newRecord);
  }
  return reactiveRecord;
}
let currentReactiveObserver = null;
function valueMutated(target, key) {
  const reactiveRecord = TargetToReactiveRecordMap.get(target);
  if (!isUndefined$1(reactiveRecord)) {
    const reactiveObservers = reactiveRecord[key];
    if (!isUndefined$1(reactiveObservers)) {
      for (let i = 0, len = reactiveObservers.length; i < len; i += 1) {
        const ro = reactiveObservers[i];
        ro.notify();
      }
    }
  }
}
function valueObserved(target, key) {
  // We should determine if an active Observing Record is present to track mutations.
  if (currentReactiveObserver === null) {
    return;
  }
  const ro = currentReactiveObserver;
  const reactiveRecord = getReactiveRecord(target);
  let reactiveObservers = reactiveRecord[key];
  if (isUndefined$1(reactiveObservers)) {
    reactiveObservers = [];
    reactiveRecord[key] = reactiveObservers;
  } else if (reactiveObservers[0] === ro) {
    return; // perf optimization considering that most subscriptions will come from the same record
  }
  if (ArrayIndexOf.call(reactiveObservers, ro) === -1) {
    ro.link(reactiveObservers);
  }
}
class ReactiveObserver {
  constructor(callback) {
    this.listeners = [];
    this.callback = callback;
  }
  observe(job) {
    const inceptionReactiveRecord = currentReactiveObserver;
    // eslint-disable-next-line @typescript-eslint/no-this-alias
    currentReactiveObserver = this;
    let error;
    try {
      job();
    } catch (e) {
      error = Object(e);
    } finally {
      currentReactiveObserver = inceptionReactiveRecord;
      if (error !== undefined) {
        throw error; // eslint-disable-line no-unsafe-finally
      }
    }
  }
  /**
   * This method is responsible for disconnecting the Reactive Observer
   * from any Reactive Record that has a reference to it, to prevent future
   * notifications about previously recorded access.
   */
  reset() {
    const {
      listeners
    } = this;
    const len = listeners.length;
    if (len > 0) {
      for (let i = 0; i < len; i++) {
        const set = listeners[i];
        const setLength = set.length;
        // The length is usually 1, so avoid doing an indexOf when we know for certain
        // that `this` is the first item in the array.
        if (setLength > 1) {
          // Swap with the last item before removal.
          // (Avoiding splice here is a perf optimization, and the order doesn't matter.)
          const index = ArrayIndexOf.call(set, this);
          set[index] = set[setLength - 1];
        }
        // Remove the last item
        ArrayPop.call(set);
      }
      listeners.length = 0;
    }
  }
  // friend methods
  notify() {
    this.callback.call(undefined, this);
  }
  link(reactiveObservers) {
    ArrayPush$1.call(reactiveObservers, this);
    // we keep track of observing records where the observing record was added to so we can do some clean up later on
    ArrayPush$1.call(this.listeners, reactiveObservers);
  }
  isObserving() {
    return currentReactiveObserver === this;
  }
}

/*
 * Copyright (c) 2024, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
/**
 * This map keeps track of objects to signals. There is an assumption that the signal is strongly referenced
 * on the object which allows the SignalTracker to be garbage collected along with the object.
 */
const TargetToSignalTrackerMap = new WeakMap();
function getSignalTracker(target) {
  let signalTracker = TargetToSignalTrackerMap.get(target);
  if (isUndefined$1(signalTracker)) {
    signalTracker = new SignalTracker();
    TargetToSignalTrackerMap.set(target, signalTracker);
  }
  return signalTracker;
}
function subscribeToSignal(target, signal, update) {
  const signalTracker = getSignalTracker(target);
  if (isFalse(signalTracker.seen(signal))) {
    signalTracker.subscribeToSignal(signal, update);
  }
}
function unsubscribeFromSignals(target) {
  if (TargetToSignalTrackerMap.has(target)) {
    const signalTracker = getSignalTracker(target);
    signalTracker.unsubscribeFromSignals();
    signalTracker.reset();
  }
}
/**
 * A normalized string representation of an error, because browsers behave differently
 */
const errorWithStack = err => {
  if (typeof err !== 'object' || err === null) {
    return String(err);
  }
  const stack = 'stack' in err ? String(err.stack) : '';
  const message = 'message' in err ? String(err.message) : '';
  const constructor = err.constructor.name;
  return stack.includes(message) ? stack : `${constructor}: ${message}\n${stack}`;
};
/**
 * This class is used to keep track of the signals associated to a given object.
 * It is used to prevent the LWC engine from subscribing duplicate callbacks multiple times
 * to the same signal. Additionally, it keeps track of all signal unsubscribe callbacks, handles invoking
 * them when necessary and discarding them.
 */
class SignalTracker {
  constructor() {
    this.signalToUnsubscribeMap = new Map();
  }
  seen(signal) {
    return this.signalToUnsubscribeMap.has(signal);
  }
  subscribeToSignal(signal, update) {
    try {
      const unsubscribe = signal.subscribe(update);
      if (isFunction$1(unsubscribe)) {
        // TODO [#3978]: Evaluate how we should handle the case when unsubscribe is not a function.
        // Long term we should throw an error or log a warning.
        this.signalToUnsubscribeMap.set(signal, unsubscribe);
      }
    } catch (err) {
      logWarnOnce(`Attempted to subscribe to an object that has the shape of a signal but received the following error: ${errorWithStack(err)}`);
    }
  }
  unsubscribeFromSignals() {
    try {
      this.signalToUnsubscribeMap.forEach(unsubscribe => unsubscribe());
    } catch (err) {
      logWarnOnce(`Attempted to call a signal's unsubscribe callback but received the following error: ${errorWithStack(err)}`);
    }
  }
  reset() {
    this.signalToUnsubscribeMap.clear();
  }
}
function componentValueMutated(vm, key) {
  // On the server side, we don't need mutation tracking. Skipping it improves performance.
  {
    valueMutated(vm.component, key);
  }
}
function componentValueObserved(vm, key, target = {}) {
  const {
    component,
    tro
  } = vm;
  // On the server side, we don't need mutation tracking. Skipping it improves performance.
  {
    valueObserved(component, key);
  }
  // The portion of reactivity that's exposed to signals is to subscribe a callback to re-render the VM (templates).
  // We check the following to ensure re-render is subscribed at the correct time.
  //  1. The template is currently being rendered (there is a template reactive observer)
  //  2. There was a call to a getter to access the signal (happens during vnode generation)
  if (lwcRuntimeFlags.ENABLE_EXPERIMENTAL_SIGNALS && isObject(target) && !isNull(target) && true &&
  // Only subscribe if a template is being rendered by the engine
  tro.isObserving()) {
    if (isTrustedSignal()) {
      // Subscribe the template reactive observer's notify method, which will mark the vm as dirty and schedule hydration.
      subscribeToSignal(component, target, tro.notify.bind(tro));
    }
  }
}
function createReactiveObserver(callback) {
  // On the server side, we don't need mutation tracking. Skipping it improves performance.
  return new ReactiveObserver(callback);
}

/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
function resolveCircularModuleDependency(fn) {
  const module = fn();
  return module?.__esModule ? module.default : module;
}
function isCircularModuleDependency(obj) {
  return isFunction$1(obj) && hasOwnProperty$1.call(obj, '__circular__');
}

/*
 * Copyright (c) 2023, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const instrumentDef = globalThis.__lwc_instrument_cmp_def ?? noop;
const instrumentInstance = globalThis.__lwc_instrument_cmp_instance ?? noop;

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
// This is a temporary workaround to get the @lwc/engine-server to evaluate in node without having
// to inject at runtime.
const HTMLElementConstructor = typeof HTMLElement !== 'undefined' ? HTMLElement : function () {};
const HTMLElementPrototype = HTMLElementConstructor.prototype;

/*
 * Copyright (c) 2023, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
// Apply ARIA string reflection behavior to a prototype.
// This is deliberately kept separate from @lwc/aria-reflection. @lwc/aria-reflection is a global polyfill that is
// needed for backwards compatibility in LEX, whereas this is designed to only apply to our own
// LightningElement/BaseBridgeElement prototypes.
// Note we only need to handle ARIA reflections that aren't already in Element.prototype
const ariaReflectionPolyfillDescriptors = create(null);
for (const [propName, attrName] of entries(AriaPropNameToAttrNameMap)) {
  if (isUndefined$1(getPropertyDescriptor(HTMLElementPrototype, propName))) {
    // Note that we need to call this.{get,set,has,remove}Attribute rather than dereferencing
    // from Element.prototype, because these methods are overridden in LightningElement.
    ariaReflectionPolyfillDescriptors[propName] = {
      get() {
        return this.getAttribute(attrName);
      },
      set(newValue) {
        // TODO [#3284]: According to the spec, IDL nullable type values
        // (null and undefined) should remove the attribute; however, we
        // only do so in the case of null for historical reasons.
        // See also https://github.com/w3c/aria/issues/1858
        if (isNull(newValue)) {
          this.removeAttribute(attrName);
        } else {
          this.setAttribute(attrName, newValue);
        }
      },
      // configurable and enumerable to allow it to be overridden – this mimics Safari's/Chrome's behavior
      configurable: true,
      enumerable: true
    };
  }
}
// Add descriptors for ARIA attributes
for (const [attrName, propName] of entries(AriaAttrNameToPropNameMap)) {}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
/**
 * This is a descriptor map that contains
 * all standard properties that a Custom Element can support (including AOM properties), which
 * determines what kind of capabilities the Base HTML Element and
 * Base Lightning Element should support.
 */
const HTMLElementOriginalDescriptors = create(null);
forEach.call(keys(AriaPropNameToAttrNameMap), propName => {
  // Note: intentionally using our in-house getPropertyDescriptor instead of getOwnPropertyDescriptor here because
  // in IE11, some properties are on Element.prototype instead of HTMLElement, just to be sure.
  const descriptor = getPropertyDescriptor(HTMLElementPrototype, propName);
  if (!isUndefined$1(descriptor)) {
    HTMLElementOriginalDescriptors[propName] = descriptor;
  }
});
for (const propName of REFLECTIVE_GLOBAL_PROPERTY_SET) {
  // Note: intentionally using our in-house getPropertyDescriptor instead of getOwnPropertyDescriptor here because
  // in IE11, id property is on Element.prototype instead of HTMLElement, and we suspect that more will fall into
  // this category, so, better to be sure.
  const descriptor = getPropertyDescriptor(HTMLElementPrototype, propName);
  if (!isUndefined$1(descriptor)) {
    HTMLElementOriginalDescriptors[propName] = descriptor;
  }
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
function updateComponentValue(vm, key, newValue) {
  const {
    cmpFields
  } = vm;
  if (newValue !== cmpFields[key]) {
    cmpFields[key] = newValue;
    componentValueMutated(vm, key);
  }
}

/**
 * Copyright (C) 2017 salesforce.com, inc.
 */
const {
  isArray
} = Array;
const {
  prototype: ObjectDotPrototype,
  getPrototypeOf,
  create: ObjectCreate,
  defineProperty: ObjectDefineProperty,
  isExtensible,
  getOwnPropertyDescriptor,
  getOwnPropertyNames,
  getOwnPropertySymbols,
  preventExtensions,
  hasOwnProperty
} = Object;
const {
  push: ArrayPush,
  concat: ArrayConcat
} = Array.prototype;
function isUndefined(obj) {
  return obj === undefined;
}
function isFunction(obj) {
  return typeof obj === 'function';
}
const proxyToValueMap = new WeakMap();
function registerProxy(proxy, value) {
  proxyToValueMap.set(proxy, value);
}
const unwrap$1 = replicaOrAny => proxyToValueMap.get(replicaOrAny) || replicaOrAny;
class BaseProxyHandler {
  constructor(membrane, value) {
    this.originalTarget = value;
    this.membrane = membrane;
  }
  // Shared utility methods
  wrapDescriptor(descriptor) {
    if (hasOwnProperty.call(descriptor, 'value')) {
      descriptor.value = this.wrapValue(descriptor.value);
    } else {
      const {
        set: originalSet,
        get: originalGet
      } = descriptor;
      if (!isUndefined(originalGet)) {
        descriptor.get = this.wrapGetter(originalGet);
      }
      if (!isUndefined(originalSet)) {
        descriptor.set = this.wrapSetter(originalSet);
      }
    }
    return descriptor;
  }
  copyDescriptorIntoShadowTarget(shadowTarget, key) {
    const {
      originalTarget
    } = this;
    // Note: a property might get defined multiple times in the shadowTarget
    //       but it will always be compatible with the previous descriptor
    //       to preserve the object invariants, which makes these lines safe.
    const originalDescriptor = getOwnPropertyDescriptor(originalTarget, key);
    // TODO: it should be impossible for the originalDescriptor to ever be undefined, this `if` can be removed
    /* istanbul ignore else */
    if (!isUndefined(originalDescriptor)) {
      const wrappedDesc = this.wrapDescriptor(originalDescriptor);
      ObjectDefineProperty(shadowTarget, key, wrappedDesc);
    }
  }
  lockShadowTarget(shadowTarget) {
    const {
      originalTarget
    } = this;
    const targetKeys = ArrayConcat.call(getOwnPropertyNames(originalTarget), getOwnPropertySymbols(originalTarget));
    targetKeys.forEach(key => {
      this.copyDescriptorIntoShadowTarget(shadowTarget, key);
    });
    const {
      membrane: {
        tagPropertyKey
      }
    } = this;
    if (!isUndefined(tagPropertyKey) && !hasOwnProperty.call(shadowTarget, tagPropertyKey)) {
      ObjectDefineProperty(shadowTarget, tagPropertyKey, ObjectCreate(null));
    }
    preventExtensions(shadowTarget);
  }
  // Shared Traps
  // TODO: apply() is never called
  /* istanbul ignore next */
  apply(shadowTarget, thisArg, argArray) {
    /* No op */
  }
  // TODO: construct() is never called
  /* istanbul ignore next */
  construct(shadowTarget, argArray, newTarget) {
    /* No op */
  }
  get(shadowTarget, key) {
    const {
      originalTarget,
      membrane: {
        valueObserved
      }
    } = this;
    const value = originalTarget[key];
    valueObserved(originalTarget, key);
    return this.wrapValue(value);
  }
  has(shadowTarget, key) {
    const {
      originalTarget,
      membrane: {
        tagPropertyKey,
        valueObserved
      }
    } = this;
    valueObserved(originalTarget, key);
    // since key is never going to be undefined, and tagPropertyKey might be undefined
    // we can simply compare them as the second part of the condition.
    return key in originalTarget || key === tagPropertyKey;
  }
  ownKeys(shadowTarget) {
    const {
      originalTarget,
      membrane: {
        tagPropertyKey
      }
    } = this;
    // if the membrane tag key exists and it is not in the original target, we add it to the keys.
    const keys = isUndefined(tagPropertyKey) || hasOwnProperty.call(originalTarget, tagPropertyKey) ? [] : [tagPropertyKey];
    // small perf optimization using push instead of concat to avoid creating an extra array
    ArrayPush.apply(keys, getOwnPropertyNames(originalTarget));
    ArrayPush.apply(keys, getOwnPropertySymbols(originalTarget));
    return keys;
  }
  isExtensible(shadowTarget) {
    const {
      originalTarget
    } = this;
    // optimization to avoid attempting to lock down the shadowTarget multiple times
    if (!isExtensible(shadowTarget)) {
      return false; // was already locked down
    }
    if (!isExtensible(originalTarget)) {
      this.lockShadowTarget(shadowTarget);
      return false;
    }
    return true;
  }
  getPrototypeOf(shadowTarget) {
    const {
      originalTarget
    } = this;
    return getPrototypeOf(originalTarget);
  }
  getOwnPropertyDescriptor(shadowTarget, key) {
    const {
      originalTarget,
      membrane: {
        valueObserved,
        tagPropertyKey
      }
    } = this;
    // keys looked up via getOwnPropertyDescriptor need to be reactive
    valueObserved(originalTarget, key);
    let desc = getOwnPropertyDescriptor(originalTarget, key);
    if (isUndefined(desc)) {
      if (key !== tagPropertyKey) {
        return undefined;
      }
      // if the key is the membrane tag key, and is not in the original target,
      // we produce a synthetic descriptor and install it on the shadow target
      desc = {
        value: undefined,
        writable: false,
        configurable: false,
        enumerable: false
      };
      ObjectDefineProperty(shadowTarget, tagPropertyKey, desc);
      return desc;
    }
    if (desc.configurable === false) {
      // updating the descriptor to non-configurable on the shadow
      this.copyDescriptorIntoShadowTarget(shadowTarget, key);
    }
    // Note: by accessing the descriptor, the key is marked as observed
    // but access to the value, setter or getter (if available) cannot observe
    // mutations, just like regular methods, in which case we just do nothing.
    return this.wrapDescriptor(desc);
  }
}
const getterMap$1 = new WeakMap();
const setterMap$1 = new WeakMap();
const reverseGetterMap = new WeakMap();
const reverseSetterMap = new WeakMap();
class ReactiveProxyHandler extends BaseProxyHandler {
  wrapValue(value) {
    return this.membrane.getProxy(value);
  }
  wrapGetter(originalGet) {
    const wrappedGetter = getterMap$1.get(originalGet);
    if (!isUndefined(wrappedGetter)) {
      return wrappedGetter;
    }
    const handler = this;
    const get = function () {
      // invoking the original getter with the original target
      return handler.wrapValue(originalGet.call(unwrap$1(this)));
    };
    getterMap$1.set(originalGet, get);
    reverseGetterMap.set(get, originalGet);
    return get;
  }
  wrapSetter(originalSet) {
    const wrappedSetter = setterMap$1.get(originalSet);
    if (!isUndefined(wrappedSetter)) {
      return wrappedSetter;
    }
    const set = function (v) {
      // invoking the original setter with the original target
      originalSet.call(unwrap$1(this), unwrap$1(v));
    };
    setterMap$1.set(originalSet, set);
    reverseSetterMap.set(set, originalSet);
    return set;
  }
  unwrapDescriptor(descriptor) {
    if (hasOwnProperty.call(descriptor, 'value')) {
      // dealing with a data descriptor
      descriptor.value = unwrap$1(descriptor.value);
    } else {
      const {
        set,
        get
      } = descriptor;
      if (!isUndefined(get)) {
        descriptor.get = this.unwrapGetter(get);
      }
      if (!isUndefined(set)) {
        descriptor.set = this.unwrapSetter(set);
      }
    }
    return descriptor;
  }
  unwrapGetter(redGet) {
    const reverseGetter = reverseGetterMap.get(redGet);
    if (!isUndefined(reverseGetter)) {
      return reverseGetter;
    }
    const handler = this;
    const get = function () {
      // invoking the red getter with the proxy of this
      return unwrap$1(redGet.call(handler.wrapValue(this)));
    };
    getterMap$1.set(get, redGet);
    reverseGetterMap.set(redGet, get);
    return get;
  }
  unwrapSetter(redSet) {
    const reverseSetter = reverseSetterMap.get(redSet);
    if (!isUndefined(reverseSetter)) {
      return reverseSetter;
    }
    const handler = this;
    const set = function (v) {
      // invoking the red setter with the proxy of this
      redSet.call(handler.wrapValue(this), handler.wrapValue(v));
    };
    setterMap$1.set(set, redSet);
    reverseSetterMap.set(redSet, set);
    return set;
  }
  set(shadowTarget, key, value) {
    const {
      originalTarget,
      membrane: {
        valueMutated
      }
    } = this;
    const oldValue = originalTarget[key];
    if (oldValue !== value) {
      originalTarget[key] = value;
      valueMutated(originalTarget, key);
    } else if (key === 'length' && isArray(originalTarget)) {
      // fix for issue #236: push will add the new index, and by the time length
      // is updated, the internal length is already equal to the new length value
      // therefore, the oldValue is equal to the value. This is the forking logic
      // to support this use case.
      valueMutated(originalTarget, key);
    }
    return true;
  }
  deleteProperty(shadowTarget, key) {
    const {
      originalTarget,
      membrane: {
        valueMutated
      }
    } = this;
    delete originalTarget[key];
    valueMutated(originalTarget, key);
    return true;
  }
  setPrototypeOf(shadowTarget, prototype) {}
  preventExtensions(shadowTarget) {
    if (isExtensible(shadowTarget)) {
      const {
        originalTarget
      } = this;
      preventExtensions(originalTarget);
      // if the originalTarget is a proxy itself, it might reject
      // the preventExtension call, in which case we should not attempt to lock down
      // the shadow target.
      // TODO: It should not actually be possible to reach this `if` statement.
      // If a proxy rejects extensions, then calling preventExtensions will throw an error:
      // https://codepen.io/nolanlawson-the-selector/pen/QWMOjbY
      /* istanbul ignore if */
      if (isExtensible(originalTarget)) {
        return false;
      }
      this.lockShadowTarget(shadowTarget);
    }
    return true;
  }
  defineProperty(shadowTarget, key, descriptor) {
    const {
      originalTarget,
      membrane: {
        valueMutated,
        tagPropertyKey
      }
    } = this;
    if (key === tagPropertyKey && !hasOwnProperty.call(originalTarget, key)) {
      // To avoid leaking the membrane tag property into the original target, we must
      // be sure that the original target doesn't have yet.
      // NOTE: we do not return false here because Object.freeze and equivalent operations
      // will attempt to set the descriptor to the same value, and expect no to throw. This
      // is an small compromise for the sake of not having to diff the descriptors.
      return true;
    }
    ObjectDefineProperty(originalTarget, key, this.unwrapDescriptor(descriptor));
    // intentionally testing if false since it could be undefined as well
    if (descriptor.configurable === false) {
      this.copyDescriptorIntoShadowTarget(shadowTarget, key);
    }
    valueMutated(originalTarget, key);
    return true;
  }
  /*LWC compiler v9.2.2*/
}
const getterMap = new WeakMap();
const setterMap = new WeakMap();
class ReadOnlyHandler extends BaseProxyHandler {
  wrapValue(value) {
    return this.membrane.getReadOnlyProxy(value);
  }
  wrapGetter(originalGet) {
    const wrappedGetter = getterMap.get(originalGet);
    if (!isUndefined(wrappedGetter)) {
      return wrappedGetter;
    }
    const handler = this;
    const get = function () {
      // invoking the original getter with the original target
      return handler.wrapValue(originalGet.call(unwrap$1(this)));
    };
    getterMap.set(originalGet, get);
    return get;
  }
  wrapSetter(originalSet) {
    const wrappedSetter = setterMap.get(originalSet);
    if (!isUndefined(wrappedSetter)) {
      return wrappedSetter;
    }
    const set = function (v) {};
    setterMap.set(originalSet, set);
    return set;
  }
  set(shadowTarget, key, value) {
    /* istanbul ignore next */
    return false;
  }
  deleteProperty(shadowTarget, key) {
    /* istanbul ignore next */
    return false;
  }
  setPrototypeOf(shadowTarget, prototype) {}
  preventExtensions(shadowTarget) {
    /* istanbul ignore next */
    return false;
  }
  defineProperty(shadowTarget, key, descriptor) {
    /* istanbul ignore next */
    return false;
  }
  /*LWC compiler v9.2.2*/
}
function defaultValueIsObservable(value) {
  // intentionally checking for null
  if (value === null) {
    return false;
  }
  // treat all non-object types, including undefined, as non-observable values
  if (typeof value !== 'object') {
    return false;
  }
  if (isArray(value)) {
    return true;
  }
  const proto = getPrototypeOf(value);
  return proto === ObjectDotPrototype || proto === null || getPrototypeOf(proto) === null;
}
const defaultValueObserved = (obj, key) => {
  /* do nothing */
};
const defaultValueMutated = (obj, key) => {
  /* do nothing */
};
function createShadowTarget(value) {
  return isArray(value) ? [] : {};
}
class ObservableMembrane {
  constructor(options = {}) {
    this.readOnlyObjectGraph = new WeakMap();
    this.reactiveObjectGraph = new WeakMap();
    const {
      valueMutated,
      valueObserved,
      valueIsObservable,
      tagPropertyKey
    } = options;
    this.valueMutated = isFunction(valueMutated) ? valueMutated : defaultValueMutated;
    this.valueObserved = isFunction(valueObserved) ? valueObserved : defaultValueObserved;
    this.valueIsObservable = isFunction(valueIsObservable) ? valueIsObservable : defaultValueIsObservable;
    this.tagPropertyKey = tagPropertyKey;
  }
  getProxy(value) {
    const unwrappedValue = unwrap$1(value);
    if (this.valueIsObservable(unwrappedValue)) {
      // When trying to extract the writable version of a readonly we return the readonly.
      if (this.readOnlyObjectGraph.get(unwrappedValue) === value) {
        return value;
      }
      return this.getReactiveHandler(unwrappedValue);
    }
    return unwrappedValue;
  }
  getReadOnlyProxy(value) {
    value = unwrap$1(value);
    if (this.valueIsObservable(value)) {
      return this.getReadOnlyHandler(value);
    }
    return value;
  }
  unwrapProxy(p) {
    return unwrap$1(p);
  }
  getReactiveHandler(value) {
    let proxy = this.reactiveObjectGraph.get(value);
    if (isUndefined(proxy)) {
      // caching the proxy after the first time it is accessed
      const handler = new ReactiveProxyHandler(this, value);
      proxy = new Proxy(createShadowTarget(value), handler);
      registerProxy(proxy, value);
      this.reactiveObjectGraph.set(value, proxy);
    }
    return proxy;
  }
  getReadOnlyHandler(value) {
    let proxy = this.readOnlyObjectGraph.get(value);
    if (isUndefined(proxy)) {
      // caching the proxy after the first time it is accessed
      const handler = new ReadOnlyHandler(this, value);
      proxy = new Proxy(createShadowTarget(value), handler);
      registerProxy(proxy, value);
      this.readOnlyObjectGraph.set(value, proxy);
    }
    return proxy;
  }
}
/** version: 2.0.0 */

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const lockerLivePropertyKey = Symbol.for('@@lockerLiveValue');
const reactiveMembrane = new ObservableMembrane({
  valueObserved,
  valueMutated,
  tagPropertyKey: lockerLivePropertyKey
});
function getReadOnlyProxy(value) {
  // We must return a frozen wrapper around the value, so that child components cannot mutate properties passed to
  // them from their parents. This applies to both the client and server.
  return reactiveMembrane.getReadOnlyProxy(value);
}
function getReactiveProxy(value) {
  // On the server side, we don't need mutation tracking. Skipping it improves performance.
  return reactiveMembrane.getProxy(value);
}
// Making the component instance a live value when using Locker to support expandos.
function markLockerLiveObject(obj) {
  // On the server side, we don't need mutation tracking. Skipping it improves performance.
  {
    obj[lockerLivePropertyKey] = undefined;
  }
}

/*
 * Copyright (c) 2023, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
let globalStylesheet;
function isStyleElement(elm) {
  return elm.tagName === 'STYLE';
}
async function fetchStylesheet(elm) {
  if (isStyleElement(elm)) {
    return elm.textContent;
  } else {
    // <link>
    const {
      href
    } = elm;
    try {
      return await (await fetch(href)).text();
    } catch (_err) {
      logWarnOnce(`Ignoring cross-origin stylesheet in migrate mode: ${href}`);
      // ignore errors with cross-origin stylesheets - nothing we can do for those
      return '';
    }
  }
}
function initGlobalStylesheet() {
  const stylesheet = new CSSStyleSheet();
  const elmsToPromises = new Map();
  let lastSeenLength = 0;
  const copyToGlobalStylesheet = () => {
    const elms = document.head.querySelectorAll('style:not([data-rendered-by-lwc]),link[rel="stylesheet"]');
    if (elms.length === lastSeenLength) {
      return; // nothing to update
    }
    lastSeenLength = elms.length;
    const promises = [...elms].map(elm => {
      let promise = elmsToPromises.get(elm);
      if (!promise) {
        // Cache the promise
        promise = fetchStylesheet(elm);
        elmsToPromises.set(elm, promise);
      }
      return promise;
    });
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    Promise.all(promises).then(stylesheetTexts => {
      // When replaceSync() is called, the entire contents of the constructable stylesheet are replaced
      // with the copied+concatenated styles. This means that any shadow root's adoptedStyleSheets that
      // contains this constructable stylesheet will immediately get the new styles.
      stylesheet.replaceSync(stylesheetTexts.join('\n'));
    });
  };
  const headObserver = new MutationObserver(copyToGlobalStylesheet);
  // By observing only the childList, note that we are not covering the case where someone changes an `href`
  // on an existing <link>`, or the textContent on an existing `<style>`. This is assumed to be an uncommon
  // case and not worth covering.
  headObserver.observe(document.head, {
    childList: true
  });
  copyToGlobalStylesheet();
  return stylesheet;
}
function applyShadowMigrateMode(shadowRoot) {
  if (!globalStylesheet) {
    globalStylesheet = initGlobalStylesheet();
  }
  shadowRoot.synthetic = true; // pretend to be synthetic mode
  shadowRoot.adoptedStyleSheets.push(globalStylesheet);
}

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
/**
 * This module is responsible for producing the ComponentDef object that is always
 * accessible via `vm.def`. This is lazily created during the creation of the first
 * instance of a component class, and shared across all instances.
 *
 * This structure can be used to synthetically create proxies, and understand the
 * shape of a component. It is also used internally to apply extra optimizations.
 */
/**
 * This operation is called with a descriptor of an standard html property
 * that a Custom Element can support (including AOM properties), which
 * determines what kind of capabilities the Base Lightning Element should support. When producing the new descriptors
 * for the Base Lightning Element, it also include the reactivity bit, so the standard property is reactive.
 * @param propName
 * @param descriptor
 */
function createBridgeToElementDescriptor(propName, descriptor) {
  const {
    get,
    set,
    enumerable,
    configurable
  } = descriptor;
  if (!isFunction$1(get)) {
    throw new TypeError(`Detected invalid public property descriptor for HTMLElement.prototype.${propName} definition. Missing the standard getter.`);
  }
  if (!isFunction$1(set)) {
    throw new TypeError(`Detected invalid public property descriptor for HTMLElement.prototype.${propName} definition. Missing the standard setter.`);
  }
  return {
    enumerable,
    configurable,
    get() {
      const vm = getAssociatedVM(this);
      if (isBeingConstructed(vm)) {
        return;
      }
      componentValueObserved(vm, propName);
      return get.call(vm.elm);
    },
    set(newValue) {
      const vm = getAssociatedVM(this);
      updateComponentValue(vm, propName, newValue);
      return set.call(vm.elm, newValue);
    }
  };
}
const refsCache = new WeakMap();
/**
 * This class is the base class for any LWC element.
 * Some elements directly extends this class, others implement it via inheritance.
 */
// @ts-expect-error When exported, it will conform, but we need to build it first!
const LightningElement = function () {
  // This should be as performant as possible, while any initialization should be done lazily
  if (isNull(vmBeingConstructed)) {
    // Thrown when doing something like `new LightningElement()` or
    // `class Foo extends LightningElement {}; new Foo()`
    throw new TypeError('Illegal constructor');
  }
  // This is a no-op unless Lightning DevTools are enabled.
  instrumentInstance(this, vmBeingConstructed);
  const vm = vmBeingConstructed;
  const {
    def,
    elm
  } = vm;
  const {
    bridge
  } = def;
  setPrototypeOf(elm, bridge.prototype);
  vm.component = this;
  // Locker hooks assignment. When the LWC engine run with Locker, Locker intercepts all the new
  // component creation and passes hooks to instrument all the component interactions with the
  // engine. We are intentionally hiding this argument from the formal API of LightningElement
  // because we don't want folks to know about it just yet.
  if (arguments.length === 1) {
    const {
      callHook,
      setHook,
      getHook
    } = arguments[0];
    vm.callHook = callHook;
    vm.setHook = setHook;
    vm.getHook = getHook;
  }
  markLockerLiveObject(this);
  // Linking elm, shadow root and component with the VM.
  associateVM(this, vm);
  associateVM(elm, vm);
  if (vm.renderMode === 1 /* RenderMode.Shadow */) {
    vm.renderRoot = doAttachShadow(vm);
  } else {
    vm.renderRoot = elm;
  }
  return this;
};
function doAttachShadow(vm) {
  const {
    elm,
    mode,
    shadowMode,
    def: {
      ctor
    },
    renderer: {
      attachShadow
    }
  } = vm;
  const shadowRoot = attachShadow(elm, {
    [KEY__SYNTHETIC_MODE]: shadowMode === 1 /* ShadowMode.Synthetic */,
    delegatesFocus: Boolean(ctor.delegatesFocus),
    mode
  });
  vm.shadowRoot = shadowRoot;
  associateVM(shadowRoot, vm);
  if (lwcRuntimeFlags.ENABLE_FORCE_SHADOW_MIGRATE_MODE && vm.shadowMigrateMode) {
    applyShadowMigrateMode(shadowRoot);
  }
  return shadowRoot;
}
// Type assertion because we need to build the prototype before it satisfies the interface.
LightningElement.prototype = {
  constructor: LightningElement,
  dispatchEvent(event) {
    const vm = getAssociatedVM(this);
    const {
      elm,
      renderer: {
        dispatchEvent
      }
    } = vm;
    return dispatchEvent(elm, event);
  },
  addEventListener(type, listener, options) {
    const vm = getAssociatedVM(this);
    const {
      elm,
      renderer: {
        addEventListener
      }
    } = vm;
    const wrappedListener = getWrappedComponentsListener(vm, listener);
    addEventListener(elm, type, wrappedListener, options);
  },
  removeEventListener(type, listener, options) {
    const vm = getAssociatedVM(this);
    const {
      elm,
      renderer: {
        removeEventListener
      }
    } = vm;
    const wrappedListener = getWrappedComponentsListener(vm, listener);
    removeEventListener(elm, type, wrappedListener, options);
  },
  hasAttribute(name) {
    const vm = getAssociatedVM(this);
    const {
      elm,
      renderer: {
        getAttribute
      }
    } = vm;
    return !isNull(getAttribute(elm, name));
  },
  hasAttributeNS(namespace, name) {
    const vm = getAssociatedVM(this);
    const {
      elm,
      renderer: {
        getAttribute
      }
    } = vm;
    return !isNull(getAttribute(elm, name, namespace));
  },
  removeAttribute(name) {
    const vm = getAssociatedVM(this);
    const {
      elm,
      renderer: {
        removeAttribute
      }
    } = vm;
    removeAttribute(elm, name);
  },
  removeAttributeNS(namespace, name) {
    const {
      elm,
      renderer: {
        removeAttribute
      }
    } = getAssociatedVM(this);
    removeAttribute(elm, name, namespace);
  },
  getAttribute(name) {
    const vm = getAssociatedVM(this);
    const {
      elm
    } = vm;
    const {
      getAttribute
    } = vm.renderer;
    return getAttribute(elm, name);
  },
  getAttributeNS(namespace, name) {
    const vm = getAssociatedVM(this);
    const {
      elm
    } = vm;
    const {
      getAttribute
    } = vm.renderer;
    return getAttribute(elm, name, namespace);
  },
  setAttribute(name, value) {
    const vm = getAssociatedVM(this);
    const {
      elm,
      renderer: {
        setAttribute
      }
    } = vm;
    setAttribute(elm, name, value);
  },
  setAttributeNS(namespace, name, value) {
    const vm = getAssociatedVM(this);
    const {
      elm,
      renderer: {
        setAttribute
      }
    } = vm;
    setAttribute(elm, name, value, namespace);
  },
  getBoundingClientRect() {
    const vm = getAssociatedVM(this);
    const {
      elm,
      renderer: {
        getBoundingClientRect
      }
    } = vm;
    return getBoundingClientRect(elm);
  },
  attachInternals() {
    const vm = getAssociatedVM(this);
    const {
      def: {
        ctor
      },
      elm,
      apiVersion,
      renderer: {
        attachInternals
      }
    } = vm;
    if (!isAPIFeatureEnabled(7 /* APIFeature.ENABLE_ELEMENT_INTERNALS_AND_FACE */, apiVersion)) {
      throw new Error(`The attachInternals API is only supported in API version 61 and above. ` + `The current version is ${apiVersion}. ` + `To use this API, update the LWC component API version. https://lwc.dev/guide/versioning`);
    }
    const internals = attachInternals(elm);
    if (vm.shadowMode === 1 /* ShadowMode.Synthetic */ && supportsSyntheticElementInternals(ctor)) {
      const handler = {
        get(target, prop) {
          if (prop === 'shadowRoot') {
            return vm.shadowRoot;
          }
          const value = Reflect.get(target, prop);
          if (typeof value === 'function') {
            return value.bind(target);
          }
          return value;
        },
        set(target, prop, value) {
          return Reflect.set(target, prop, value);
        }
      };
      return new Proxy(internals, handler);
    } else if (vm.shadowMode === 1 /* ShadowMode.Synthetic */) {
      throw new Error('attachInternals API is not supported in synthetic shadow.');
    }
    return internals;
  },
  get isConnected() {
    const vm = getAssociatedVM(this);
    const {
      elm,
      renderer: {
        isConnected
      }
    } = vm;
    return isConnected(elm);
  },
  get classList() {
    const vm = getAssociatedVM(this);
    const {
      elm,
      renderer: {
        getClassList
      }
    } = vm;
    return getClassList(elm);
  },
  get template() {
    const vm = getAssociatedVM(this);
    return vm.shadowRoot;
  },
  get hostElement() {
    const vm = getAssociatedVM(this);
    const apiVersion = getComponentAPIVersion(vm.def.ctor);
    if (!isAPIFeatureEnabled(8 /* APIFeature.ENABLE_THIS_DOT_HOST_ELEMENT */, apiVersion)) {
      // Simulate the old behavior for `this.hostElement` to avoid a breaking change
      return undefined;
    }
    return vm.elm;
  },
  get refs() {
    const vm = getAssociatedVM(this);
    if (isUpdatingTemplate) {
      // If the template is in the process of being updated, then we don't want to go through the normal
      // process of returning the refs and caching them, because the state of the refs is unstable.
      // This can happen if e.g. a template contains `<div class={foo}></div>` and `foo` is computed
      // based on `this.refs.bar`.
      return;
    }
    const {
      refVNodes,
      cmpTemplate
    } = vm;
    // For backwards compatibility with component written before template refs
    // were introduced, we return undefined if the template has no refs defined
    // anywhere. This fixes components that may want to add an expando called `refs`
    // and are checking if it exists with `if (this.refs)`  before adding it.
    // Note we use a null refVNodes to indicate that the template has no refs defined.
    if (isNull(refVNodes)) {
      return;
    }
    // The refNodes can be cached based on the refVNodes, since the refVNodes
    // are recreated from scratch every time the template is rendered.
    // This happens with `vm.refVNodes = null` in `template.ts` in `@lwc/engine-core`.
    let refs = refsCache.get(refVNodes);
    if (isUndefined$1(refs)) {
      refs = create(null);
      for (const key of keys(refVNodes)) {
        refs[key] = refVNodes[key].elm;
      }
      freeze(refs);
      refsCache.set(refVNodes, refs);
    }
    return refs;
  },
  // For backwards compat, we allow component authors to set `refs` as an expando
  set refs(value) {
    defineProperty(this, 'refs', {
      configurable: true,
      enumerable: true,
      writable: true,
      value
    });
  },
  get shadowRoot() {
    // From within the component instance, the shadowRoot is always reported as "closed".
    // Authors should rely on this.template instead.
    return null;
  },
  get children() {
    const vm = getAssociatedVM(this);
    const renderer = vm.renderer;
    return renderer.getChildren(vm.elm);
  },
  get childNodes() {
    const vm = getAssociatedVM(this);
    const renderer = vm.renderer;
    // getChildNodes returns a NodeList, which has `item(index: number): Node | null`.
    // NodeListOf<T> extends NodeList, but claims to not return null. That seems inaccurate,
    // but these are built-in types, so ultimately not our problem.
    return renderer.getChildNodes(vm.elm);
  },
  get firstChild() {
    const vm = getAssociatedVM(this);
    const renderer = vm.renderer;
    return renderer.getFirstChild(vm.elm);
  },
  get firstElementChild() {
    const vm = getAssociatedVM(this);
    const renderer = vm.renderer;
    return renderer.getFirstElementChild(vm.elm);
  },
  get lastChild() {
    const vm = getAssociatedVM(this);
    const renderer = vm.renderer;
    return renderer.getLastChild(vm.elm);
  },
  get lastElementChild() {
    const vm = getAssociatedVM(this);
    const renderer = vm.renderer;
    return renderer.getLastElementChild(vm.elm);
  },
  get ownerDocument() {
    const vm = getAssociatedVM(this);
    const renderer = vm.renderer;
    return renderer.ownerDocument(vm.elm);
  },
  get tagName() {
    const {
      elm,
      renderer
    } = getAssociatedVM(this);
    return renderer.getTagName(elm);
  },
  get style() {
    const {
      elm,
      renderer,
      def
    } = getAssociatedVM(this);
    const apiVersion = getComponentAPIVersion(def.ctor);
    if (!isAPIFeatureEnabled(9 /* APIFeature.ENABLE_THIS_DOT_STYLE */, apiVersion)) {
      // Simulate the old behavior for `this.style` to avoid a breaking change
      return undefined;
    }
    return renderer.getStyle(elm);
  },
  render() {
    const vm = getAssociatedVM(this);
    return vm.def.template;
  },
  toString() {
    const vm = getAssociatedVM(this);
    return `[object ${vm.def.name}]`;
  }
};
const queryAndChildGetterDescriptors = create(null);
const queryMethods = ['getElementsByClassName', 'getElementsByTagName', 'querySelector', 'querySelectorAll'];
// Generic passthrough for query APIs on HTMLElement to the relevant Renderer APIs
for (const queryMethod of queryMethods) {
  queryAndChildGetterDescriptors[queryMethod] = {
    value(arg) {
      const vm = getAssociatedVM(this);
      const {
        elm,
        renderer
      } = vm;
      return renderer[queryMethod](elm, arg);
    },
    configurable: true,
    enumerable: true,
    writable: true
  };
}
defineProperties(LightningElement.prototype, queryAndChildGetterDescriptors);
const lightningBasedDescriptors = create(null);
for (const propName in HTMLElementOriginalDescriptors) {
  lightningBasedDescriptors[propName] = createBridgeToElementDescriptor(propName, HTMLElementOriginalDescriptors[propName]);
}
// Apply ARIA reflection to LightningElement.prototype, on both the browser and server.
// This allows `this.aria*` property accessors to work from inside a component, and to reflect `aria-*` attrs.
// Note this works regardless of whether the global ARIA reflection polyfill is applied or not.
{
  // In the browser, we use createBridgeToElementDescriptor, so we can get the normal reactivity lifecycle for
  // aria* properties
  for (const [propName, descriptor] of entries(ariaReflectionPolyfillDescriptors)) {
    lightningBasedDescriptors[propName] = createBridgeToElementDescriptor(propName, descriptor);
  }
}
defineProperties(LightningElement.prototype, lightningBasedDescriptors);
defineProperty(LightningElement, 'CustomElementConstructor', {
  get() {
    // If required, a runtime-specific implementation must be defined.
    throw new ReferenceError('The current runtime does not support CustomElementConstructor.');
  },
  configurable: true
});

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
function createObservedFieldPropertyDescriptor(key) {
  return {
    get() {
      const vm = getAssociatedVM(this);
      const val = vm.cmpFields[key];
      componentValueObserved(vm, key, val);
      return val;
    },
    set(newValue) {
      const vm = getAssociatedVM(this);
      updateComponentValue(vm, key, newValue);
    },
    enumerable: true,
    configurable: true
  };
}

/*
 * Copyright (c) 2023, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const AdapterToTokenMap = new Map();
function createContextWatcher(vm, wireDef, callbackWhenContextIsReady) {
  const {
    adapter
  } = wireDef;
  const adapterContextToken = AdapterToTokenMap.get(adapter);
  if (isUndefined$1(adapterContextToken)) {
    return; // no provider found, nothing to be done
  }
  const {
    elm,
    context: {
      wiredConnecting,
      wiredDisconnecting
    },
    renderer: {
      registerContextConsumer
    }
  } = vm;
  // waiting for the component to be connected to formally request the context via the token
  ArrayPush$1.call(wiredConnecting, () => {
    // This will attempt to connect the current element with one of its anscestors
    // that can provide context for the given wire adapter. This relationship is
    // keyed on the secret & internal value of `adapterContextToken`, which is unique
    // to a given wire adapter.
    //
    // Depending on the runtime environment, this connection is made using either DOM
    // events (in the browser) or a custom traversal (on the server).
    registerContextConsumer(elm, adapterContextToken, {
      setNewContext(newContext) {
        // eslint-disable-next-line @lwc/lwc-internal/no-invalid-todo
        // TODO: dev-mode validation of config based on the adapter.contextSchema
        callbackWhenContextIsReady(newContext);
        // Return true as the context is always consumed here and the consumer should
        // stop bubbling.
        return true;
      },
      setDisconnectedCallback(disconnectCallback) {
        // adds this callback into the disconnect bucket so it gets disconnected from parent
        // the the element hosting the wire is disconnected
        ArrayPush$1.call(wiredDisconnecting, disconnectCallback);
      }
    });
  });
}

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const DeprecatedWiredElementHost = '$$DeprecatedWiredElementHostKey$$';
const DeprecatedWiredParamsMeta = '$$DeprecatedWiredParamsMetaKey$$';
const WireMetaMap = new Map();
function createFieldDataCallback(vm, name) {
  return value => {
    updateComponentValue(vm, name, value);
  };
}
function createMethodDataCallback(vm, method) {
  return value => {
    // dispatching new value into the wired method
    runWithBoundaryProtection(vm, vm.owner, noop, () => {
      // job
      method.call(vm.component, value);
    }, noop);
  };
}
function createConfigWatcher(component, configCallback, callbackWhenConfigIsReady) {
  let hasPendingConfig = false;
  // creating the reactive observer for reactive params when needed
  const ro = createReactiveObserver(() => {
    if (hasPendingConfig === false) {
      hasPendingConfig = true;
      // collect new config in the micro-task
      // eslint-disable-next-line @typescript-eslint/no-floating-promises
      Promise.resolve().then(() => {
        hasPendingConfig = false;
        // resetting current reactive params
        ro.reset();
        // dispatching a new config due to a change in the configuration
        computeConfigAndUpdate();
      });
    }
  });
  const computeConfigAndUpdate = () => {
    let config;
    ro.observe(() => config = configCallback(component));
    // eslint-disable-next-line @lwc/lwc-internal/no-invalid-todo
    // TODO: dev-mode validation of config based on the adapter.configSchema
    // @ts-expect-error it is assigned in the observe() callback
    callbackWhenConfigIsReady(config);
  };
  return {
    computeConfigAndUpdate,
    ro
  };
}
function createConnector(vm, name, wireDef) {
  const {
    method,
    adapter,
    configCallback,
    dynamic
  } = wireDef;
  let debugInfo;
  const fieldOrMethodCallback = isUndefined$1(method) ? createFieldDataCallback(vm, name) : createMethodDataCallback(vm, method);
  const dataCallback = value => {
    fieldOrMethodCallback(value);
  };
  let context;
  let connector;
  // Workaround to pass the component element associated to this wire adapter instance.
  defineProperty(dataCallback, DeprecatedWiredElementHost, {
    value: vm.elm
  });
  defineProperty(dataCallback, DeprecatedWiredParamsMeta, {
    value: dynamic
  });
  runWithBoundaryProtection(vm, vm, noop, () => {
    // job
    connector = new adapter(dataCallback, {
      tagName: vm.tagName
    });
  }, noop);
  const updateConnectorConfig = config => {
    // every time the config is recomputed due to tracking,
    // this callback will be invoked with the new computed config
    runWithBoundaryProtection(vm, vm, noop, () => {
      // job
      if ("production" !== 'production') ;
      connector.update(config, context);
    }, noop);
  };
  // Computes the current wire config and calls the update method on the wire adapter.
  // If it has params, we will need to observe changes in the next tick.
  const {
    computeConfigAndUpdate,
    ro
  } = createConfigWatcher(vm.component, configCallback, updateConnectorConfig);
  // if the adapter needs contextualization, we need to watch for new context and push it alongside the config
  if (!isUndefined$1(adapter.contextSchema)) {
    createContextWatcher(vm, wireDef, newContext => {
      // every time the context is pushed into this component,
      // this callback will be invoked with the new computed context
      if (context !== newContext) {
        context = newContext;
        // Note: when new context arrives, the config will be recomputed and pushed along side the new
        // context, this is to preserve the identity characteristics, config should not have identity
        // (ever), while context can have identity
        if (vm.state === 1 /* VMState.connected */) {
          computeConfigAndUpdate();
        }
      }
    });
  }
  return {
    // @ts-expect-error the boundary protection executes sync, connector is always defined
    connector,
    computeConfigAndUpdate,
    resetConfigWatcher: () => ro.reset()
  };
}
function storeWiredMethodMeta(descriptor, adapter, configCallback, dynamic) {
  // support for callable adapters
  if (adapter.adapter) {
    adapter = adapter.adapter;
  }
  const method = descriptor.value;
  const def = {
    adapter,
    method,
    configCallback,
    dynamic
  };
  WireMetaMap.set(descriptor, def);
}
function storeWiredFieldMeta(descriptor, adapter, configCallback, dynamic) {
  // support for callable adapters
  if (adapter.adapter) {
    adapter = adapter.adapter;
  }
  const def = {
    adapter,
    configCallback,
    dynamic
  };
  WireMetaMap.set(descriptor, def);
}
function installWireAdapters(vm) {
  const {
    context,
    def: {
      wire
    }
  } = vm;
  const wiredConnecting = context.wiredConnecting = [];
  const wiredDisconnecting = context.wiredDisconnecting = [];
  for (const fieldNameOrMethod in wire) {
    const descriptor = wire[fieldNameOrMethod];
    const wireDef = WireMetaMap.get(descriptor);
    if (!isUndefined$1(wireDef)) {
      const {
        connector,
        computeConfigAndUpdate,
        resetConfigWatcher
      } = createConnector(vm, fieldNameOrMethod, wireDef);
      const hasDynamicParams = wireDef.dynamic.length > 0;
      ArrayPush$1.call(wiredConnecting, () => {
        connector.connect();
        if (!lwcRuntimeFlags.ENABLE_WIRE_SYNC_EMIT) {
          if (hasDynamicParams) {
            // eslint-disable-next-line @typescript-eslint/no-floating-promises
            Promise.resolve().then(computeConfigAndUpdate);
            return;
          }
        }
        computeConfigAndUpdate();
      });
      ArrayPush$1.call(wiredDisconnecting, () => {
        connector.disconnect();
        resetConfigWatcher();
      });
    }
  }
}
function connectWireAdapters(vm) {
  const {
    wiredConnecting
  } = vm.context;
  for (let i = 0, len = wiredConnecting.length; i < len; i += 1) {
    wiredConnecting[i]();
  }
}
function disconnectWireAdapters(vm) {
  const {
    wiredDisconnecting
  } = vm.context;
  runWithBoundaryProtection(vm, vm, noop, () => {
    // job
    for (let i = 0, len = wiredDisconnecting.length; i < len; i += 1) {
      wiredDisconnecting[i]();
    }
  }, noop);
}
function createPublicPropertyDescriptor(key) {
  return {
    get() {
      const vm = getAssociatedVM(this);
      if (isBeingConstructed(vm)) {
        return;
      }
      const val = vm.cmpProps[key];
      componentValueObserved(vm, key, val);
      return val;
    },
    set(newValue) {
      const vm = getAssociatedVM(this);
      vm.cmpProps[key] = newValue;
      componentValueMutated(vm, key);
    },
    enumerable: true,
    configurable: true
  };
}
function createPublicAccessorDescriptor(key, descriptor) {
  const {
    get,
    set,
    enumerable,
    configurable
  } = descriptor;
  assert.invariant(isFunction$1(get), `Invalid public accessor ${toString(key)} decorated with @api. The property is missing a getter.`);
  return {
    get() {
      return get.call(this);
    },
    set(newValue) {
      getAssociatedVM(this);
      if (set) {
        set.call(this, newValue);
      }
    },
    enumerable,
    configurable
  };
}
function internalTrackDecorator(key) {
  return {
    get() {
      const vm = getAssociatedVM(this);
      const val = vm.cmpFields[key];
      componentValueObserved(vm, key, val);
      return val;
    },
    set(newValue) {
      const vm = getAssociatedVM(this);
      const reactiveOrAnyValue = getReactiveProxy(newValue);
      updateComponentValue(vm, key, reactiveOrAnyValue);
    },
    enumerable: true,
    configurable: true
  };
}
function internalWireFieldDecorator(key) {
  return {
    get() {
      const vm = getAssociatedVM(this);
      componentValueObserved(vm, key);
      return vm.cmpFields[key];
    },
    set(value) {
      const vm = getAssociatedVM(this);
      /**
       * Reactivity for wired fields is provided in wiring.
       * We intentionally add reactivity here since this is just
       * letting the author to do the wrong thing, but it will keep our
       * system to be backward compatible.
       */
      updateComponentValue(vm, key, value);
    },
    enumerable: true,
    configurable: true
  };
}
/**
 * INTERNAL: This function can only be invoked by compiled code. The compiler
 * will prevent this function from being imported by user-land code.
 * @param Ctor
 * @param meta
 */
function registerDecorators(Ctor, meta) {
  const proto = Ctor.prototype;
  const {
    publicProps,
    publicMethods,
    wire,
    track,
    fields
  } = meta;
  const apiMethods = create(null);
  const apiFields = create(null);
  const wiredMethods = create(null);
  const wiredFields = create(null);
  const observedFields = create(null);
  const apiFieldsConfig = create(null);
  let descriptor;
  if (!isUndefined$1(publicProps)) {
    for (const fieldName in publicProps) {
      const propConfig = publicProps[fieldName];
      apiFieldsConfig[fieldName] = propConfig.config;
      descriptor = getOwnPropertyDescriptor$1(proto, fieldName);
      if (propConfig.config > 0) {
        if (isUndefined$1(descriptor)) {
          // TODO [#3441]: This line of code does not seem possible to reach.
          throw new Error();
        }
        descriptor = createPublicAccessorDescriptor(fieldName, descriptor);
      } else {
        // [W-9927596] If a component has both a public property and a private setter/getter
        // with the same name, the property is defined as a public accessor. This branch is
        // only here for backward compatibility reasons.
        if (!isUndefined$1(descriptor) && !isUndefined$1(descriptor.get)) {
          descriptor = createPublicAccessorDescriptor(fieldName, descriptor);
        } else {
          descriptor = createPublicPropertyDescriptor(fieldName);
        }
      }
      apiFields[fieldName] = descriptor;
      defineProperty(proto, fieldName, descriptor);
    }
  }
  if (!isUndefined$1(publicMethods)) {
    forEach.call(publicMethods, methodName => {
      descriptor = getOwnPropertyDescriptor$1(proto, methodName);
      if (isUndefined$1(descriptor)) {
        throw new Error();
      }
      apiMethods[methodName] = descriptor;
    });
  }
  if (!isUndefined$1(wire)) {
    for (const fieldOrMethodName in wire) {
      const {
        adapter,
        method,
        config: configCallback,
        dynamic = []
      } = wire[fieldOrMethodName];
      descriptor = getOwnPropertyDescriptor$1(proto, fieldOrMethodName);
      if (method === 1) {
        if (isUndefined$1(descriptor)) {
          throw new Error(`Missing descriptor for wired method "${fieldOrMethodName}".`);
        }
        wiredMethods[fieldOrMethodName] = descriptor;
        storeWiredMethodMeta(descriptor, adapter, configCallback, dynamic);
      } else {
        descriptor = internalWireFieldDecorator(fieldOrMethodName);
        wiredFields[fieldOrMethodName] = descriptor;
        storeWiredFieldMeta(descriptor, adapter, configCallback, dynamic);
        defineProperty(proto, fieldOrMethodName, descriptor);
      }
    }
  }
  if (!isUndefined$1(track)) {
    for (const fieldName in track) {
      descriptor = getOwnPropertyDescriptor$1(proto, fieldName);
      descriptor = internalTrackDecorator(fieldName);
      defineProperty(proto, fieldName, descriptor);
    }
  }
  if (!isUndefined$1(fields)) {
    for (let i = 0, n = fields.length; i < n; i++) {
      const fieldName = fields[i];
      descriptor = getOwnPropertyDescriptor$1(proto, fieldName);
      // [W-9927596] Only mark a field as observed whenever it isn't a duplicated public nor
      // tracked property. This is only here for backward compatibility purposes.
      const isDuplicatePublicProp = !isUndefined$1(publicProps) && fieldName in publicProps;
      const isDuplicateTrackedProp = !isUndefined$1(track) && fieldName in track;
      if (!isDuplicatePublicProp && !isDuplicateTrackedProp) {
        observedFields[fieldName] = createObservedFieldPropertyDescriptor(fieldName);
      }
    }
  }
  setDecoratorsMeta(Ctor, {
    apiMethods,
    apiFields,
    apiFieldsConfig,
    wiredMethods,
    wiredFields,
    observedFields
  });
  return Ctor;
}
const signedDecoratorToMetaMap = new Map();
function setDecoratorsMeta(Ctor, meta) {
  signedDecoratorToMetaMap.set(Ctor, meta);
}
const defaultMeta = {
  apiMethods: EmptyObject,
  apiFields: EmptyObject,
  apiFieldsConfig: EmptyObject,
  wiredMethods: EmptyObject,
  wiredFields: EmptyObject,
  observedFields: EmptyObject
};
function getDecoratorsMeta(Ctor) {
  const meta = signedDecoratorToMetaMap.get(Ctor);
  return isUndefined$1(meta) ? defaultMeta : meta;
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const signedTemplateSet = new Set();
function defaultEmptyTemplate() {
  return [];
}
signedTemplateSet.add(defaultEmptyTemplate);
function isTemplateRegistered(tpl) {
  return signedTemplateSet.has(tpl);
}
/**
 * INTERNAL: This function can only be invoked by compiled code. The compiler
 * will prevent this function from being imported by userland code.
 * @param tpl
 */
function registerTemplate(tpl) {
  signedTemplateSet.add(tpl);
  // chaining this method as a way to wrap existing
  // assignment of templates easily, without too much transformation
  return tpl;
}

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
/**
 * This module is responsible for creating the base bridge class BaseBridgeElement
 * that represents the HTMLElement extension used for any LWC inserted in the DOM.
 */
// A bridge descriptor is a descriptor whose job is just to get the component instance
// from the element instance, and get the value or set a new value on the component.
// This means that across different elements, similar names can get the exact same
// descriptor, so we can cache them:
const cachedGetterByKey = create(null);
const cachedSetterByKey = create(null);
function createGetter(key) {
  let fn = cachedGetterByKey[key];
  if (isUndefined$1(fn)) {
    fn = cachedGetterByKey[key] = function () {
      const vm = getAssociatedVM(this);
      const {
        getHook
      } = vm;
      return getHook(vm.component, key);
    };
  }
  return fn;
}
function createSetter(key) {
  let fn = cachedSetterByKey[key];
  if (isUndefined$1(fn)) {
    fn = cachedSetterByKey[key] = function (newValue) {
      const vm = getAssociatedVM(this);
      const {
        setHook
      } = vm;
      newValue = getReadOnlyProxy(newValue);
      setHook(vm.component, key, newValue);
    };
  }
  return fn;
}
function createMethodCaller(methodName) {
  return function () {
    const vm = getAssociatedVM(this);
    const {
      callHook,
      component
    } = vm;
    const fn = component[methodName];
    return callHook(vm.component, fn, ArraySlice.call(arguments));
  };
}
function createAttributeChangedCallback(attributeToPropMap, superAttributeChangedCallback) {
  return function attributeChangedCallback(attrName, oldValue, newValue) {
    if (oldValue === newValue) {
      // Ignore same values.
      return;
    }
    const propName = attributeToPropMap[attrName];
    if (isUndefined$1(propName)) {
      if (!isUndefined$1(superAttributeChangedCallback)) {
        // delegate unknown attributes to the super.
        // Typescript does not like it when you treat the `arguments` object as an array
        // @ts-expect-error type-mismatch
        superAttributeChangedCallback.apply(this, arguments);
      }
      return;
    }
    // Reflect attribute change to the corresponding property when changed from outside.
    this[propName] = newValue;
  };
}
function HTMLBridgeElementFactory(SuperClass, publicProperties, methods, observedFields, proto, hasCustomSuperClass) {
  const HTMLBridgeElement = class extends SuperClass {
    /*LWC compiler v9.2.2*/
  };
  // generating the hash table for attributes to avoid duplicate fields and facilitate validation
  // and false positives in case of inheritance.
  const attributeToPropMap = create(null);
  const {
    attributeChangedCallback: superAttributeChangedCallback
  } = SuperClass.prototype;
  const {
    observedAttributes: superObservedAttributes = []
  } = SuperClass;
  const descriptors = create(null);
  // expose getters and setters for each public props on the new Element Bridge
  for (let i = 0, len = publicProperties.length; i < len; i += 1) {
    const propName = publicProperties[i];
    attributeToPropMap[htmlPropertyToAttribute(propName)] = propName;
    descriptors[propName] = {
      get: createGetter(propName),
      set: createSetter(propName),
      enumerable: true,
      configurable: true
    };
  }
  // expose public methods as props on the new Element Bridge
  for (let i = 0, len = methods.length; i < len; i += 1) {
    const methodName = methods[i];
    descriptors[methodName] = {
      value: createMethodCaller(methodName),
      writable: true,
      configurable: true
    };
  }
  // creating a new attributeChangedCallback per bridge because they are bound to the corresponding
  // map of attributes to props. We do this after all other props and methods to avoid the possibility
  // of getting overrule by a class declaration in user-land, and we make it non-writable, non-configurable
  // to preserve this definition.
  descriptors.attributeChangedCallback = {
    value: createAttributeChangedCallback(attributeToPropMap, superAttributeChangedCallback)
  };
  // To avoid leaking private component details, accessing internals from outside a component is not allowed.
  descriptors.attachInternals = {
    set() {
    },
    get() {
    }
  };
  descriptors.formAssociated = {
    set() {
    },
    get() {
    }
  };
  // Specify attributes for which we want to reflect changes back to their corresponding
  // properties via attributeChangedCallback.
  defineProperty(HTMLBridgeElement, 'observedAttributes', {
    get() {
      return [...superObservedAttributes, ...keys(attributeToPropMap)];
    }
  });
  defineProperties(HTMLBridgeElement.prototype, descriptors);
  return HTMLBridgeElement;
}
// We do some special handling of non-standard ARIA props like ariaLabelledBy as well as props without (as of this
// writing) broad cross-browser support like ariaBrailleLabel. This is so the reflection works correctly and preserves
// backwards compatibility with the previous global polyfill approach.
//
// The goal here is to expose `elm.aria*` property accessors to work from outside a component, and to reflect `aria-*`
// attrs. This is especially important because the template compiler compiles aria-* attrs on components to aria* props.
// Note this works regardless of whether the global ARIA reflection polyfill is applied or not.
//
// Also note this ARIA reflection only really makes sense in the browser. On the server, there is no
// `renderedCallback()`, so you cannot do e.g. `this.template.querySelector('x-child').ariaBusy = 'true'`. So we don't
// need to expose ARIA props outside the LightningElement
const basePublicProperties = [...getOwnPropertyNames$1(HTMLElementOriginalDescriptors), ...getOwnPropertyNames$1(ariaReflectionPolyfillDescriptors)];
const BaseBridgeElement = HTMLBridgeElementFactory(HTMLElementConstructor, basePublicProperties, []);
freeze(BaseBridgeElement);
seal(BaseBridgeElement.prototype);

/*
 * Copyright (c) 2025, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const VALID_SCOPE_TOKEN_REGEX = /^[a-zA-Z0-9\-_]+$/;
function getOrCreateAbortSignal(cssContent) {
  return undefined;
}
function makeHostToken(token) {
  // Note: if this ever changes, update the `cssScopeTokens` returned by `@lwc/compiler`
  return `${token}-host`;
}
function createInlineStyleVNode(content) {
  return api.h('style', {
    key: 'style',
    // special key
    attrs: {
      type: 'text/css'
    }
  }, [api.t(content)]);
}
// TODO [#3733]: remove support for legacy scope tokens
function updateStylesheetToken(vm, template, legacy) {
  const {
    elm,
    context,
    renderMode,
    shadowMode,
    renderer: {
      getClassList,
      removeAttribute,
      setAttribute
    }
  } = vm;
  const {
    stylesheets: newStylesheets
  } = template;
  const newStylesheetToken = legacy ? template.legacyStylesheetToken : template.stylesheetToken;
  const {
    stylesheets: newVmStylesheets
  } = vm;
  const isSyntheticShadow = renderMode === 1 /* RenderMode.Shadow */ && shadowMode === 1 /* ShadowMode.Synthetic */;
  const {
    hasScopedStyles
  } = context;
  let newToken;
  let newHasTokenInClass;
  let newHasTokenInAttribute;
  // Reset the styling token applied to the host element.
  let oldToken;
  let oldHasTokenInClass;
  let oldHasTokenInAttribute;
  if (legacy) {
    oldToken = context.legacyStylesheetToken;
    oldHasTokenInClass = context.hasLegacyTokenInClass;
    oldHasTokenInAttribute = context.hasLegacyTokenInAttribute;
  } else {
    oldToken = context.stylesheetToken;
    oldHasTokenInClass = context.hasTokenInClass;
    oldHasTokenInAttribute = context.hasTokenInAttribute;
  }
  if (!isUndefined$1(oldToken)) {
    if (oldHasTokenInClass) {
      getClassList(elm).remove(makeHostToken(oldToken));
    }
    if (oldHasTokenInAttribute) {
      removeAttribute(elm, makeHostToken(oldToken));
    }
  }
  // Apply the new template styling token to the host element, if the new template has any
  // associated stylesheets. In the case of light DOM, also ensure there is at least one scoped stylesheet.
  const hasNewStylesheets = hasStyles(newStylesheets);
  const hasNewVmStylesheets = hasStyles(newVmStylesheets);
  if (hasNewStylesheets || hasNewVmStylesheets) {
    newToken = newStylesheetToken;
  }
  // Set the new styling token on the host element
  if (!isUndefined$1(newToken)) {
    if (hasScopedStyles) {
      const hostScopeTokenClass = makeHostToken(newToken);
      getClassList(elm).add(hostScopeTokenClass);
      newHasTokenInClass = true;
    }
    if (isSyntheticShadow) {
      setAttribute(elm, makeHostToken(newToken), '');
      newHasTokenInAttribute = true;
    }
  }
  // Update the styling tokens present on the context object.
  if (legacy) {
    context.legacyStylesheetToken = newToken;
    context.hasLegacyTokenInClass = newHasTokenInClass;
    context.hasLegacyTokenInAttribute = newHasTokenInAttribute;
  } else {
    context.stylesheetToken = newToken;
    context.hasTokenInClass = newHasTokenInClass;
    context.hasTokenInAttribute = newHasTokenInAttribute;
  }
}
function evaluateStylesheetsContent(stylesheets, stylesheetToken, vm) {
  const content = [];
  let root;
  for (let i = 0; i < stylesheets.length; i++) {
    let stylesheet = stylesheets[i];
    if (isArray$1(stylesheet)) {
      ArrayPush$1.apply(content, evaluateStylesheetsContent(stylesheet, stylesheetToken, vm));
    } else {
      const isScopedCss = isTrue(stylesheet[KEY__SCOPED_CSS]);
      const isNativeOnlyCss = isTrue(stylesheet[KEY__NATIVE_ONLY_CSS]);
      const {
        renderMode,
        shadowMode
      } = vm;
      if (lwcRuntimeFlags.DISABLE_LIGHT_DOM_UNSCOPED_CSS && !isScopedCss && renderMode === 0 /* RenderMode.Light */) {
        logError('Unscoped CSS is not supported in Light DOM in this environment. Please use scoped CSS ' + '(*.scoped.css) instead of unscoped CSS (*.css). See also: https://sfdc.co/scoped-styles-light-dom');
        continue;
      }
      // Apply the scope token only if the stylesheet itself is scoped, or if we're rendering synthetic shadow.
      const scopeToken = isScopedCss || shadowMode === 1 /* ShadowMode.Synthetic */ && renderMode === 1 /* RenderMode.Shadow */ ? stylesheetToken : undefined;
      // Use the actual `:host` selector if we're rendering global CSS for light DOM, or if we're rendering
      // native shadow DOM. Synthetic shadow DOM never uses `:host`.
      const useActualHostSelector = renderMode === 0 /* RenderMode.Light */ ? !isScopedCss : shadowMode === 0 /* ShadowMode.Native */;
      // Use the native :dir() pseudoclass only in native shadow DOM. Otherwise, in synthetic shadow,
      // we use an attribute selector on the host to simulate :dir().
      let useNativeDirPseudoclass;
      if (renderMode === 1 /* RenderMode.Shadow */) {
        useNativeDirPseudoclass = shadowMode === 0 /* ShadowMode.Native */;
      } else {
        // Light DOM components should only render `[dir]` if they're inside of a synthetic shadow root.
        // At the top level (root is null) or inside of a native shadow root, they should use `:dir()`.
        if (isUndefined$1(root)) {
          // Only calculate the root once as necessary
          root = getNearestShadowComponent(vm);
        }
        useNativeDirPseudoclass = isNull(root) || root.shadowMode === 0 /* ShadowMode.Native */;
      }
      let cssContent;
      if (isNativeOnlyCss && renderMode === 1 /* RenderMode.Shadow */ && shadowMode === 1 /* ShadowMode.Synthetic */) {
        // Native-only (i.e. disableSyntheticShadowSupport) CSS should be ignored entirely
        // in synthetic shadow. It's fine to use in either native shadow or light DOM, but in
        // synthetic shadow it wouldn't be scoped properly and so should be ignored.
        cssContent = '/* ignored native-only CSS */';
      } else {
        cssContent = stylesheet(scopeToken, useActualHostSelector, useNativeDirPseudoclass);
      }
      ArrayPush$1.call(content, cssContent);
    }
  }
  return content;
}
function getStylesheetsContent(vm, template) {
  const {
    stylesheets,
    stylesheetToken
  } = template;
  const {
    stylesheets: vmStylesheets
  } = vm;
  if (!isUndefined$1(stylesheetToken) && !isValidScopeToken(stylesheetToken)) {
    throw new Error('stylesheet token must be a valid string');
  }
  const hasTemplateStyles = hasStyles(stylesheets);
  const hasVmStyles = hasStyles(vmStylesheets);
  if (hasTemplateStyles) {
    const content = evaluateStylesheetsContent(stylesheets, stylesheetToken, vm);
    if (hasVmStyles) {
      // Slow path – merge the template styles and vm styles
      ArrayPush$1.apply(content, evaluateStylesheetsContent(vmStylesheets, stylesheetToken, vm));
    }
    return content;
  }
  if (hasVmStyles) {
    // No template styles, so return vm styles directly
    return evaluateStylesheetsContent(vmStylesheets, stylesheetToken, vm);
  }
  // Fastest path - no styles, so return an empty array
  return EmptyArray;
}
// It might be worth caching this to avoid doing the lookup repeatedly, but
// perf testing has not shown it to be a huge improvement yet:
// https://github.com/salesforce/lwc/pull/2460#discussion_r691208892
function getNearestShadowComponent(vm) {
  let owner = vm;
  while (!isNull(owner)) {
    if (owner.renderMode === 1 /* RenderMode.Shadow */) {
      return owner;
    }
    owner = owner.owner;
  }
  return owner;
}
/**
 * If the component that is currently being rendered uses scoped styles,
 * this returns the unique token for that scoped stylesheet. Otherwise
 * it returns null.
 * @param owner
 * @param legacy
 */
// TODO [#3733]: remove support for legacy scope tokens
function getScopeTokenClass(owner, legacy) {
  const {
    cmpTemplate,
    context
  } = owner;
  return context.hasScopedStyles && (legacy ? cmpTemplate?.legacyStylesheetToken : cmpTemplate?.stylesheetToken) || null;
}
function getNearestNativeShadowComponent(vm) {
  const owner = getNearestShadowComponent(vm);
  if (!isNull(owner) && owner.shadowMode === 1 /* ShadowMode.Synthetic */) {
    // Synthetic-within-native is impossible. So if the nearest shadow component is
    // synthetic, we know we won't find a native component if we go any further.
    return null;
  }
  return owner;
}
function createStylesheet(vm, stylesheets) {
  const {
    renderMode,
    shadowMode,
    renderer: {
      insertStylesheet
    }
  } = vm;
  if (renderMode === 1 /* RenderMode.Shadow */ && shadowMode === 1 /* ShadowMode.Synthetic */) {
    for (let i = 0; i < stylesheets.length; i++) {
      const stylesheet = stylesheets[i];
      insertStylesheet(stylesheet, undefined, getOrCreateAbortSignal());
    }
  } else if (vm.hydrated) {
    // Note: We need to ensure that during hydration, the stylesheets method is the same as those in ssr.
    //       This works in the client, because the stylesheets are created, and cached in the VM
    //       the first time the VM renders.
    // native shadow or light DOM, SSR
    return ArrayMap.call(stylesheets, createInlineStyleVNode);
  } else {
    // native shadow or light DOM, DOM renderer
    const root = getNearestNativeShadowComponent(vm);
    // null root means a global style
    const target = isNull(root) ? undefined : root.shadowRoot;
    for (let i = 0; i < stylesheets.length; i++) {
      const stylesheet = stylesheets[i];
      insertStylesheet(stylesheet, target, getOrCreateAbortSignal());
    }
  }
  return null;
}
function isValidScopeToken(token) {
  if (!isString(token)) {
    return false;
  }
  // See W-16614556
  return lwcRuntimeFlags.DISABLE_SCOPE_TOKEN_VALIDATION || VALID_SCOPE_TOKEN_REGEX.test(token);
}

/*
 * Copyright (c) 2023, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
/**
 * A map where the keys are weakly held and the values are a Set that are also each weakly held.
 * The goal is to avoid leaking the values, which is what would happen with a WeakMap<K, Set<V>>.
 *
 * Note that this is currently only intended to be used in dev/PRODDEBUG environments.
 *
 * This implementation relies on WeakRefs and FinalizationRegistry.
 * For some background, see: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/WeakRef
 */
class WeakMultiMap {
  constructor() {
    this._map = new WeakMap();
    this._registry = new FinalizationRegistry(weakRefs => {
      // This should be considered an optional cleanup method to remove GC'ed values from their respective arrays.
      // JS VMs are not obligated to call FinalizationRegistry callbacks.
      // Work backwards, removing stale VMs
      for (let i = weakRefs.length - 1; i >= 0; i--) {
        const vm = weakRefs[i].deref();
        if (isUndefined$1(vm)) {
          ArraySplice.call(weakRefs, i, 1); // remove
        }
      }
    });
  }
  _getWeakRefs(key) {
    let weakRefs = this._map.get(key);
    if (isUndefined$1(weakRefs)) {
      weakRefs = [];
      this._map.set(key, weakRefs);
    }
    return weakRefs;
  }
  get(key) {
    const weakRefs = this._getWeakRefs(key);
    const result = new Set();
    for (const weakRef of weakRefs) {
      const vm = weakRef.deref();
      if (!isUndefined$1(vm)) {
        result.add(vm);
      }
    }
    return result;
  }
  add(key, value) {
    const weakRefs = this._getWeakRefs(key);
    // Skip adding if already present
    for (const weakRef of weakRefs) {
      if (weakRef.deref() === value) {
        return;
      }
    }
    ArrayPush$1.call(weakRefs, new WeakRef(value));
    // It's important here not to leak the second argument, which is the "held value." The FinalizationRegistry
    // effectively creates a strong reference between the first argument (the "target") and the held value. When
    // the target is GC'ed, the callback is called, and then the held value is GC'ed.
    // Putting the key here would mean the key is not GC'ed until the value is GC'ed, which defeats the purpose
    // of the WeakMap. Whereas putting the weakRefs array here is fine, because it doesn't have a strong reference
    // to anything. See also this example:
    // https://gist.github.com/nolanlawson/79a3d36e8e6cc25c5048bb17c1795aea
    this._registry.register(value, weakRefs);
  }
  delete(key) {
    this._map.delete(key);
  }
}
let swappedStyleMap = /*@__PURE__@*/new WeakMap();
// The important thing here is the weak values – VMs are transient (one per component instance) and should be GC'ed,
// so we don't want to create strong references to them.
// The weak keys are kind of useless, because Templates, LightningElementConstructors, and Stylesheets are
// never GC'ed. But maybe they will be someday, so we may as well use weak keys too.
// The "pure" annotations are so that Rollup knows for sure it can remove these from prod mode
let activeTemplates = /*@__PURE__@*/new WeakMultiMap();
let activeComponents = /*@__PURE__@*/new WeakMultiMap();
let activeStyles = /*@__PURE__@*/new WeakMultiMap();
function getStyleOrSwappedStyle(style) {
  assertNotProd(); // this method should never leak to prod
  // TODO [#4154]: shows stale content when swapping content back and forth multiple times
  const visited = new Set();
  while (swappedStyleMap.has(style) && !visited.has(style)) {
    visited.add(style);
    style = swappedStyleMap.get(style);
  }
  return style;
}
function addActiveStylesheets(stylesheets, vm) {
  if (isUndefined$1(stylesheets) || isNull(stylesheets)) {
    // Ignore non-existent stylesheets
    return;
  }
  for (const stylesheet of flattenStylesheets(stylesheets)) {
    // this is necessary because we don't hold the list of styles
    // in the vm, we only hold the selected (already swapped template)
    // but the styles attached to the template might not be the actual
    // active ones, but the swapped versions of those.
    const swappedStylesheet = getStyleOrSwappedStyle(stylesheet);
    // this will allow us to keep track of the stylesheet that are
    // being used by a hot component
    activeStyles.add(swappedStylesheet, vm);
  }
}
function setActiveVM(vm) {
  assertNotProd(); // this method should never leak to prod
  // tracking active component
  const Ctor = vm.def.ctor;
  // this will allow us to keep track of the hot components
  activeComponents.add(Ctor, vm);
  // tracking active template
  const template = vm.cmpTemplate;
  if (!isNull(template)) {
    // this will allow us to keep track of the templates that are
    // being used by a hot component
    activeTemplates.add(template, vm);
    // Tracking active styles from the template or the VM. `template.stylesheets` are implicitly associated
    // (e.g. `foo.css` associated with `foo.html`), whereas `vm.stylesheets` are from `static stylesheets`.
    addActiveStylesheets(template.stylesheets, vm);
    addActiveStylesheets(vm.stylesheets, vm);
  }
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
/**
 * This module is responsible for producing the ComponentDef object that is always
 * accessible via `vm.def`. This is lazily created during the creation of the first
 * instance of a component class, and shared across all instances.
 *
 * This structure can be used to synthetically create proxies, and understand the
 * shape of a component. It is also used internally to apply extra optimizations.
 */
const CtorToDefMap = new WeakMap();
function getCtorProto(Ctor) {
  let proto = getPrototypeOf$1(Ctor);
  if (isNull(proto)) {
    throw new ReferenceError(`Invalid prototype chain for ${Ctor.name}, you must extend LightningElement.`);
  }
  // covering the cases where the ref is circular in AMD
  if (isCircularModuleDependency(proto)) {
    const p = resolveCircularModuleDependency(proto);
    // escape hatch for Locker and other abstractions to provide their own base class instead
    // of our Base class without having to leak it to user-land. If the circular function returns
    // itself, that's the signal that we have hit the end of the proto chain, which must always
    // be base.
    proto = p === proto ? LightningElement : p;
  }
  return proto;
}
function createComponentDef(Ctor) {
  // Enforce component-level feature flag if provided at compile time
  if (!isComponentFeatureEnabled(Ctor)) {
    const metadata = getComponentMetadata(Ctor);
    const componentName = Ctor.name || metadata?.sel || 'Unknown';
    const componentFeatureFlagPath = metadata?.componentFeatureFlag?.path || 'Unknown';
    throw new Error(`Component ${componentName} is disabled by the feature flag at ${componentFeatureFlagPath}.`);
  }
  const {
    shadowSupportMode: ctorShadowSupportMode,
    renderMode: ctorRenderMode,
    formAssociated: ctorFormAssociated
  } = Ctor;
  const decoratorsMeta = getDecoratorsMeta(Ctor);
  const {
    apiFields,
    apiFieldsConfig,
    apiMethods,
    wiredFields,
    wiredMethods,
    observedFields
  } = decoratorsMeta;
  const proto = Ctor.prototype;
  let {
    connectedCallback,
    disconnectedCallback,
    renderedCallback,
    errorCallback,
    formAssociatedCallback,
    formResetCallback,
    formDisabledCallback,
    formStateRestoreCallback,
    render
  } = proto;
  const superProto = getCtorProto(Ctor);
  const hasCustomSuperClass = superProto !== LightningElement;
  const superDef = hasCustomSuperClass ? getComponentInternalDef(superProto) : lightingElementDef;
  const bridge = HTMLBridgeElementFactory(superDef.bridge, keys(apiFields), keys(apiMethods));
  const props = assign(create(null), superDef.props, apiFields);
  const propsConfig = assign(create(null), superDef.propsConfig, apiFieldsConfig);
  const methods = assign(create(null), superDef.methods, apiMethods);
  const wire = assign(create(null), superDef.wire, wiredFields, wiredMethods);
  connectedCallback = connectedCallback || superDef.connectedCallback;
  disconnectedCallback = disconnectedCallback || superDef.disconnectedCallback;
  renderedCallback = renderedCallback || superDef.renderedCallback;
  errorCallback = errorCallback || superDef.errorCallback;
  formAssociatedCallback = formAssociatedCallback || superDef.formAssociatedCallback;
  formResetCallback = formResetCallback || superDef.formResetCallback;
  formDisabledCallback = formDisabledCallback || superDef.formDisabledCallback;
  formStateRestoreCallback = formStateRestoreCallback || superDef.formStateRestoreCallback;
  render = render || superDef.render;
  let shadowSupportMode = superDef.shadowSupportMode;
  if (!isUndefined$1(ctorShadowSupportMode)) {
    shadowSupportMode = ctorShadowSupportMode;
  }
  let renderMode = superDef.renderMode;
  if (!isUndefined$1(ctorRenderMode)) {
    renderMode = ctorRenderMode === 'light' ? 0 /* RenderMode.Light */ : 1 /* RenderMode.Shadow */;
  }
  let formAssociated = superDef.formAssociated;
  if (!isUndefined$1(ctorFormAssociated)) {
    formAssociated = ctorFormAssociated;
  }
  const template = getComponentRegisteredTemplate(Ctor) || superDef.template;
  const name = Ctor.name || superDef.name;
  // installing observed fields into the prototype.
  defineProperties(proto, observedFields);
  const def = {
    ctor: Ctor,
    name,
    wire,
    props,
    propsConfig,
    methods,
    bridge,
    template,
    renderMode,
    shadowSupportMode,
    formAssociated,
    connectedCallback,
    disconnectedCallback,
    errorCallback,
    formAssociatedCallback,
    formDisabledCallback,
    formResetCallback,
    formStateRestoreCallback,
    renderedCallback,
    render
  };
  // This is a no-op unless Lightning DevTools are enabled.
  instrumentDef(def);
  return def;
}
/**
 * EXPERIMENTAL: This function allows for the identification of LWC constructors. This API is
 * subject to change or being removed.
 * @param ctor
 */
function isComponentConstructor(ctor) {
  if (!isFunction$1(ctor)) {
    return false;
  }
  // Fast path: LightningElement is part of the prototype chain of the constructor.
  if (ctor.prototype instanceof LightningElement) {
    return true;
  }
  // Slow path: LightningElement is not part of the prototype chain of the constructor, we need
  // climb up the constructor prototype chain to check in case there are circular dependencies
  // to resolve.
  let current = ctor;
  do {
    if (isCircularModuleDependency(current)) {
      const circularResolved = resolveCircularModuleDependency(current);
      // If the circular function returns itself, that's the signal that we have hit the end
      // of the proto chain, which must always be a valid base constructor.
      if (circularResolved === current) {
        return true;
      }
      current = circularResolved;
    }
    if (current === LightningElement) {
      return true;
    }
  } while (!isNull(current) && (current = getPrototypeOf$1(current)));
  // Finally return false if the LightningElement is not part of the prototype chain.
  return false;
}
function getComponentInternalDef(Ctor) {
  let def = CtorToDefMap.get(Ctor);
  if (isUndefined$1(def)) {
    if (isCircularModuleDependency(Ctor)) {
      const resolvedCtor = resolveCircularModuleDependency(Ctor);
      def = getComponentInternalDef(resolvedCtor);
      // Cache the unresolved component ctor too. The next time if the same unresolved ctor is used,
      // look up the definition in cache instead of re-resolving and recreating the def.
      CtorToDefMap.set(Ctor, def);
      return def;
    }
    if (!isComponentConstructor(Ctor)) {
      throw new TypeError(`${Ctor} is not a valid component, or does not extends LightningElement from "lwc". You probably forgot to add the extend clause on the class declaration.`);
    }
    def = createComponentDef(Ctor);
    CtorToDefMap.set(Ctor, def);
  }
  return def;
}
function getComponentHtmlPrototype(Ctor) {
  const def = getComponentInternalDef(Ctor);
  return def.bridge;
}
const lightingElementDef = {
  name: LightningElement.name,
  props: lightningBasedDescriptors,
  propsConfig: EmptyObject,
  methods: EmptyObject,
  renderMode: 1 /* RenderMode.Shadow */,
  shadowSupportMode: 'reset',
  formAssociated: undefined,
  wire: EmptyObject,
  bridge: BaseBridgeElement,
  template: defaultEmptyTemplate,
  render: LightningElement.prototype.render
};

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
function isVBaseElement(vnode) {
  const {
    type
  } = vnode;
  return type === 2 /* VNodeType.Element */ || type === 3 /* VNodeType.CustomElement */;
}
function isSameVnode(vnode1, vnode2) {
  return vnode1.key === vnode2.key && vnode1.sel === vnode2.sel;
}
function isVCustomElement(vnode) {
  return vnode.type === 3 /* VNodeType.CustomElement */;
}
function isVFragment(vnode) {
  return vnode.type === 5 /* VNodeType.Fragment */;
}
function isVScopedSlotFragment(vnode) {
  return vnode.type === 6 /* VNodeType.ScopedSlotFragment */;
}
function isVStatic(vnode) {
  return vnode.type === 4 /* VNodeType.Static */;
}
function isVStaticPartElement(vnode) {
  return vnode.type === 1 /* VStaticPartType.Element */;
}

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const sanitizedHtmlContentSymbol = Symbol('lwc-get-sanitized-html-content');
function isSanitizedHtmlContent(object) {
  return isObject(object) && !isNull(object) && sanitizedHtmlContentSymbol in object;
}
/**
 * Wrap a pre-sanitized string designated for `.innerHTML` via `lwc:inner-html`
 * as an object with a Symbol that only we have access to.
 * @param sanitizedString
 * @returns SanitizedHtmlContent
 */
function createSanitizedHtmlContent(sanitizedString) {
  return create(null, {
    [sanitizedHtmlContentSymbol]: {
      value: sanitizedString,
      configurable: false,
      writable: false
    }
  });
}
/**
 * Safely call setProperty on an Element while handling any SanitizedHtmlContent objects correctly
 *
 * @param setProperty - renderer.setProperty
 * @param elm - Element
 * @param key - key to set
 * @param value -  value to set
 */
function safelySetProperty(setProperty, elm, key, value) {
  // See W-16614337
  // we support setting innerHTML to `undefined` because it's inherently safe
  if ((key === 'innerHTML' || key === 'outerHTML') && !isUndefined$1(value)) {
    if (isSanitizedHtmlContent(value)) {
      // it's a SanitizedHtmlContent object
      setProperty(elm, key, value[sanitizedHtmlContentSymbol]);
    }
  } else {
    setProperty(elm, key, value);
  }
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const ColonCharCode = 58;
function patchAttributes(oldVnode, vnode, renderer) {
  const {
    data,
    elm
  } = vnode;
  const {
    attrs
  } = data;
  if (isUndefined$1(attrs)) {
    return;
  }
  const oldAttrs = isNull(oldVnode) ? EmptyObject : oldVnode.data.attrs;
  // Attrs may be the same due to the static content optimization, so we can skip diffing
  if (oldAttrs === attrs) {
    return;
  }
  // Note VStaticPartData does not contain the external property so it will always default to false.
  const external = 'external' in data ? data.external : false;
  const {
    setAttribute,
    removeAttribute,
    setProperty
  } = renderer;
  for (const key in attrs) {
    const cur = attrs[key];
    const old = oldAttrs[key];
    if (old !== cur) {
      let propName;
      // For external custom elements, sniff to see if the attr should be considered a prop.
      // Use kebabCaseToCamelCase directly because we don't want to set props like `ariaLabel` or `tabIndex`
      // on a custom element versus just using the more reliable attribute format.
      if (external && (propName = kebabCaseToCamelCase(key)) in elm) {
        safelySetProperty(setProperty, elm, propName, cur);
      } else if (StringCharCodeAt.call(key, 3) === ColonCharCode) {
        // Assume xml namespace
        setAttribute(elm, key, cur, XML_NAMESPACE);
      } else if (StringCharCodeAt.call(key, 5) === ColonCharCode) {
        // Assume xlink namespace
        setAttribute(elm, key, cur, XLINK_NAMESPACE);
      } else if (isNull(cur) || isUndefined$1(cur)) {
        removeAttribute(elm, key);
      } else {
        setAttribute(elm, key, cur);
      }
    }
  }
}
function patchSlotAssignment(oldVnode, vnode, renderer) {
  const {
    slotAssignment
  } = vnode;
  if (oldVnode?.slotAssignment === slotAssignment) {
    return;
  }
  const {
    elm
  } = vnode;
  const {
    setAttribute,
    removeAttribute
  } = renderer;
  if (isUndefined$1(slotAssignment) || isNull(slotAssignment)) {
    removeAttribute(elm, 'slot');
  } else {
    setAttribute(elm, 'slot', slotAssignment);
  }
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
function isLiveBindingProp(sel, key) {
  // For properties with live bindings, we read values from the DOM element
  // instead of relying on internally tracked values.
  return sel === 'input' && (key === 'value' || key === 'checked');
}
function patchProps(oldVnode, vnode, renderer) {
  const {
    props
  } = vnode.data;
  if (isUndefined$1(props)) {
    return;
  }
  let oldProps;
  if (!isNull(oldVnode)) {
    oldProps = oldVnode.data.props;
    // Props may be the same due to the static content optimization, so we can skip diffing
    if (oldProps === props) {
      return;
    }
    if (isUndefined$1(oldProps)) {
      oldProps = EmptyObject;
    }
  }
  const isFirstPatch = isNull(oldVnode);
  const {
    elm,
    sel
  } = vnode;
  const {
    getProperty,
    setProperty
  } = renderer;
  for (const key in props) {
    const cur = props[key];
    // Set the property if it's the first time is is patched or if the previous property is
    // different than the one previously set.
    if (isFirstPatch || cur !== (isLiveBindingProp(sel, key) ? getProperty(elm, key) : oldProps[key]) || !(key in oldProps) // this is required because the above case will pass when `cur` is `undefined` and key is missing in `oldProps`
    ) {
      safelySetProperty(setProperty, elm, key, cur);
    }
  }
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const classNameToClassMap = create(null);
function getMapFromClassName(className) {
  if (isUndefined$1(className) || isNull(className) || className === '') {
    return EmptyObject;
  }
  // computed class names must be string
  // This will throw if className is a symbol or null-prototype object
  // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
  className = isString(className) ? className : className + '';
  let map = classNameToClassMap[className];
  if (map) {
    return map;
  }
  map = create(null);
  let start = 0;
  let o;
  const len = className.length;
  for (o = 0; o < len; o++) {
    if (StringCharCodeAt.call(className, o) === SPACE_CHAR) {
      if (o > start) {
        map[StringSlice.call(className, start, o)] = true;
      }
      start = o + 1;
    }
  }
  if (o > start) {
    map[StringSlice.call(className, start, o)] = true;
  }
  classNameToClassMap[className] = map;
  return map;
}
function patchClassAttribute(oldVnode, vnode, renderer) {
  const {
    elm,
    data: {
      className: newClass
    }
  } = vnode;
  const oldClass = isNull(oldVnode) ? undefined : oldVnode.data.className;
  if (oldClass === newClass) {
    return;
  }
  const newClassMap = getMapFromClassName(newClass);
  const oldClassMap = getMapFromClassName(oldClass);
  if (oldClassMap === newClassMap) {
    // These objects are cached by className string (`classNameToClassMap`), so we can only get here if there is
    // a key collision due to types, e.g. oldClass is `undefined` and newClass is `""` (empty string), or oldClass
    // is `1` (number) and newClass is `"1"` (string).
    return;
  }
  const {
    getClassList
  } = renderer;
  const classList = getClassList(elm);
  let name;
  for (name in oldClassMap) {
    // remove only if it is not in the new class collection and it is not set from within the instance
    if (isUndefined$1(newClassMap[name])) {
      classList.remove(name);
    }
  }
  for (name in newClassMap) {
    if (isUndefined$1(oldClassMap[name])) {
      classList.add(name);
    }
  }
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
// The style property is a string when defined via an expression in the template.
function patchStyleAttribute(oldVnode, vnode, renderer, owner) {
  const {
    elm,
    data: {
      style: newStyle
    }
  } = vnode;
  const oldStyle = isNull(oldVnode) ? undefined : oldVnode.data.style;
  if (oldStyle === newStyle) {
    return;
  }
  const {
    setAttribute,
    removeAttribute
  } = renderer;
  if (!isString(newStyle) || newStyle === '') {
    removeAttribute(elm, 'style');
  } else {
    setAttribute(elm, 'style', newStyle);
  }
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
function applyEventListeners(vnode, renderer) {
  const {
    elm,
    data
  } = vnode;
  const {
    on
  } = data;
  if (isUndefined$1(on)) {
    return;
  }
  const {
    addEventListener
  } = renderer;
  for (const name in on) {
    const handler = on[name];
    addEventListener(elm, name, handler);
  }
}

/*
 * Copyright (c) 2025, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
function patchDynamicEventListeners(oldVnode, vnode, renderer, owner) {
  const {
    elm,
    data: {
      dynamicOn,
      dynamicOnRaw
    },
    sel
  } = vnode;
  // dynamicOn : A cloned version of the object passed to lwc:on, with null prototype and only its own enumerable properties.
  const oldDynamicOn = oldVnode?.data?.dynamicOn ?? EmptyObject;
  const newDynamicOn = dynamicOn ?? EmptyObject;
  // dynamicOnRaw : object passed to lwc:on
  // Compare dynamicOnRaw to check if same object is passed to lwc:on
  oldVnode?.data?.dynamicOnRaw === dynamicOnRaw;
  const {
    addEventListener,
    removeEventListener
  } = renderer;
  const attachedEventListeners = getAttachedEventListeners(owner, elm);
  // Properties that are present in 'oldDynamicOn' but not in 'newDynamicOn'
  for (const eventType in oldDynamicOn) {
    if (!(eventType in newDynamicOn)) {
      // Remove listeners that were attached previously but don't have a corresponding property in `newDynamicOn`
      const attachedEventListener = attachedEventListeners[eventType];
      removeEventListener(elm, eventType, attachedEventListener);
      attachedEventListeners[eventType] = undefined;
    }
  }
  // Ensure that the event listeners that are attached match what is present in `newDynamicOn`
  for (const eventType in newDynamicOn) {
    const typeExistsInOld = eventType in oldDynamicOn;
    const newCallback = newDynamicOn[eventType];
    // Skip if callback hasn't changed
    if (typeExistsInOld && oldDynamicOn[eventType] === newCallback) {
      continue;
    }
    // Remove listener that was attached previously
    if (typeExistsInOld) {
      const attachedEventListener = attachedEventListeners[eventType];
      removeEventListener(elm, eventType, attachedEventListener);
    }
    // Bind new callback to owner component and add it as listener to element
    const newBoundEventListener = bindEventListener(owner, newCallback);
    addEventListener(elm, eventType, newBoundEventListener);
    // Store the newly added eventListener
    attachedEventListeners[eventType] = newBoundEventListener;
  }
}
function getAttachedEventListeners(vm, elm) {
  let attachedEventListeners = vm.attachedEventListeners.get(elm);
  if (isUndefined$1(attachedEventListeners)) {
    attachedEventListeners = {};
    vm.attachedEventListeners.set(elm, attachedEventListeners);
  }
  return attachedEventListeners;
}
function bindEventListener(vm, fn) {
  return function (event) {
    invokeEventListener(vm, fn, vm.component, event);
  };
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
// The HTML class property becomes the vnode.data.classMap object when defined as a string in the template.
// The compiler takes care of transforming the inline classnames into an object. It's faster to set the
// different classnames properties individually instead of via a string.
function applyStaticClassAttribute(vnode, renderer) {
  const {
    elm,
    data: {
      classMap
    }
  } = vnode;
  if (isUndefined$1(classMap)) {
    return;
  }
  const {
    getClassList
  } = renderer;
  const classList = getClassList(elm);
  for (const name in classMap) {
    classList.add(name);
  }
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
// The HTML style property becomes the vnode.data.styleDecls object when defined as a string in the template.
// The compiler takes care of transforming the inline style into an object. It's faster to set the
// different style properties individually instead of via a string.
function applyStaticStyleAttribute(vnode, renderer) {
  const {
    elm,
    data: {
      styleDecls
    }
  } = vnode;
  if (isUndefined$1(styleDecls)) {
    return;
  }
  const {
    setCSSStyleProperty
  } = renderer;
  for (let i = 0; i < styleDecls.length; i++) {
    const [prop, value, important] = styleDecls[i];
    setCSSStyleProperty(elm, prop, value, important);
  }
}

/*
 * Copyright (c) 2023, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
// Set a ref (lwc:ref) on a VM, from a template API
function applyRefs(vnode, owner) {
  const {
    data
  } = vnode;
  const {
    ref
  } = data;
  if (isUndefined$1(ref)) {
    return;
  }
  // If this method is called, then vm.refVNodes is set as the template has refs.
  // If not, then something went wrong and we threw an error above.
  const refVNodes = owner.refVNodes;
  // In cases of conflict (two elements with the same ref), prefer the last one,
  // in depth-first traversal order. This happens automatically due to how we render
  refVNodes[ref] = vnode;
}

/*
 * Copyright (c) 2024, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
function patchTextVNode(n1, n2, renderer) {
  n2.elm = n1.elm;
  if (n2.text !== n1.text) {
    updateTextContent$1(n2, renderer);
  }
}
function patchTextVStaticPart(n1, n2, renderer) {
  if (isNull(n1) || n2.text !== n1.text) {
    updateTextContent$1(n2, renderer);
  }
}
function updateTextContent$1(vnode, renderer) {
  const {
    elm,
    text
  } = vnode;
  const {
    setText
  } = renderer;
  setText(elm, text);
}

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
/**
 * Given an array of static parts, mounts the DOM element to the part based on the staticPartId
 * @param root the root element
 * @param parts an array of VStaticParts
 * @param renderer the renderer to use
 */
function traverseAndSetElements(root, parts, renderer) {
  const numParts = parts.length;
  // Optimization given that, in most cases, there will be one part, and it's just the root
  if (numParts === 1) {
    const firstPart = parts[0];
    if (firstPart.partId === 0) {
      // 0 means the root node
      firstPart.elm = root;
      return;
    }
  }
  const partIdsToParts = new Map();
  for (const staticPart of parts) {
    partIdsToParts.set(staticPart.partId, staticPart);
  }
  // Note that we traverse using `*Child`/`*Sibling` rather than `children` because the browser uses a linked
  // list under the hood to represent the DOM tree, so it's faster to do this than to create an underlying array
  // by calling `children`.
  const {
    nextSibling,
    getFirstChild,
    getParentNode
  } = renderer;
  let numFoundParts = 0;
  let partId = -1;
  // Depth-first traversal. We assign a partId to each element, which is an integer based on traversal order.
  // This function is very hot, which is why it's micro-optimized. Note we don't use a stack at all; we traverse
  // using an algorithm that relies on the parentNode getter: https://stackoverflow.com/a/5285417
  // This is very slightly faster than a TreeWalker (~0.5% on js-framework-benchmark create-10k), but basically
  // the same idea.
  let node = root;
  while (!isNull(node)) {
    // visit node
    partId++;
    const part = partIdsToParts.get(partId);
    if (!isUndefined$1(part)) {
      part.elm = node;
      numFoundParts++;
      if (numFoundParts === numParts) {
        return; // perf optimization - stop traversing once we've found everything we need
      }
    }
    const child = getFirstChild(node);
    if (!isNull(child)) {
      // walk down
      node = child;
    } else {
      let sibling;
      while (isNull(sibling = nextSibling(node))) {
        // walk up
        node = getParentNode(node);
      }
      // walk right
      node = sibling;
    }
  }
}
/**
 * Given an array of static parts, do all the mounting required for these parts.
 * @param root the root element
 * @param vnode the parent VStatic
 * @param renderer the renderer to use
 */
function mountStaticParts(root, vnode, renderer) {
  const {
    parts,
    owner
  } = vnode;
  if (isUndefined$1(parts)) {
    return;
  }
  // This adds `part.elm` to each `part`. We have to do this on every mount because the `parts`
  // array is recreated from scratch every time, so each `part.elm` is now undefined.
  traverseAndSetElements(root, parts, renderer);
  // Currently only event listeners and refs are supported for static vnodes
  for (const part of parts) {
    if (isVStaticPartElement(part)) {
      // Event listeners only need to be applied once when mounting
      applyEventListeners(part, renderer);
      // Refs must be updated after every render due to refVNodes getting reset before every render
      applyRefs(part, owner);
      patchAttributes(null, part, renderer);
      patchClassAttribute(null, part, renderer);
      patchStyleAttribute(null, part, renderer);
    } else {
      patchTextVStaticPart(null, part, renderer);
    }
  }
}
/**
 * Updates the static elements based on the content of the VStaticParts
 * @param n1 the previous VStatic vnode
 * @param n2 the current VStatic vnode
 * @param renderer the renderer to use
 */
function patchStaticParts(n1, n2, renderer) {
  const {
    parts: currParts,
    owner: currPartsOwner
  } = n2;
  if (isUndefined$1(currParts)) {
    return;
  }
  const {
    parts: prevParts
  } = n1;
  for (let i = 0; i < currParts.length; i++) {
    const prevPart = prevParts[i];
    const part = currParts[i];
    // Patch only occurs if the vnode is newly generated, which means the part.elm is always undefined
    // Since the vnode and elements are the same we can safely assume that prevParts[i].elm is defined.
    part.elm = prevPart.elm;
    if (isVStaticPartElement(part)) {
      // Refs must be updated after every render due to refVNodes getting reset before every render
      applyRefs(part, currPartsOwner);
      patchAttributes(prevPart, part, renderer);
      patchClassAttribute(prevPart, part, renderer);
      patchStyleAttribute(prevPart, part, renderer);
    } else {
      patchTextVStaticPart(null, part, renderer);
    }
  }
}

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
function patchChildren(c1, c2, parent, renderer) {
  if (hasDynamicChildren(c2)) {
    updateDynamicChildren(c1, c2, parent, renderer);
  } else {
    updateStaticChildren(c1, c2, parent, renderer);
  }
}
function patch(n1, n2, parent, renderer) {
  if (n1 === n2) {
    return;
  }
  switch (n2.type) {
    case 0 /* VNodeType.Text */:
      // VText has no special capability, fallback to the owner's renderer
      patchTextVNode(n1, n2, renderer);
      break;
    case 1 /* VNodeType.Comment */:
      // VComment has no special capability, fallback to the owner's renderer
      patchComment(n1, n2, renderer);
      break;
    case 4 /* VNodeType.Static */:
      patchStatic(n1, n2, renderer);
      break;
    case 5 /* VNodeType.Fragment */:
      patchFragment(n1, n2, parent, renderer);
      break;
    case 2 /* VNodeType.Element */:
      patchElement(n1, n2, n2.data.renderer ?? renderer);
      break;
    case 3 /* VNodeType.CustomElement */:
      patchCustomElement(n1, n2, parent, n2.data.renderer ?? renderer);
      break;
  }
}
function mount$1(node, parent, renderer, anchor) {
  switch (node.type) {
    case 0 /* VNodeType.Text */:
      // VText has no special capability, fallback to the owner's renderer
      mountText(node, parent, anchor, renderer);
      break;
    case 1 /* VNodeType.Comment */:
      // VComment has no special capability, fallback to the owner's renderer
      mountComment(node, parent, anchor, renderer);
      break;
    case 4 /* VNodeType.Static */:
      // VStatic cannot have a custom renderer associated to them, using owner's renderer
      mountStatic(node, parent, anchor, renderer);
      break;
    case 5 /* VNodeType.Fragment */:
      mountFragment(node, parent, anchor, renderer);
      break;
    case 2 /* VNodeType.Element */:
      // If the vnode data has a renderer override use it, else fallback to owner's renderer
      mountElement(node, parent, anchor, node.data.renderer ?? renderer);
      break;
    case 3 /* VNodeType.CustomElement */:
      // If the vnode data has a renderer override use it, else fallback to owner's renderer
      mountCustomElement(node, parent, anchor, node.data.renderer ?? renderer);
      break;
  }
}
function mountText(vnode, parent, anchor, renderer) {
  const {
    owner
  } = vnode;
  const {
    createText
  } = renderer;
  const textNode = vnode.elm = createText(vnode.text);
  linkNodeToShadow(textNode, owner, renderer);
  insertNode(textNode, parent, anchor, renderer);
}
function patchComment(n1, n2, renderer) {
  n2.elm = n1.elm;
  // FIXME: Comment nodes should be static, we shouldn't need to diff them together. However
  // it is the case today.
  if (n2.text !== n1.text) {
    updateTextContent$1(n2, renderer);
  }
}
function mountComment(vnode, parent, anchor, renderer) {
  const {
    owner
  } = vnode;
  const {
    createComment
  } = renderer;
  const commentNode = vnode.elm = createComment(vnode.text);
  linkNodeToShadow(commentNode, owner, renderer);
  insertNode(commentNode, parent, anchor, renderer);
}
function mountFragment(vnode, parent, anchor, renderer) {
  const {
    children
  } = vnode;
  mountVNodes(children, parent, renderer, anchor);
  vnode.elm = vnode.leading.elm;
}
function patchFragment(n1, n2, parent, renderer) {
  const {
    children,
    stable
  } = n2;
  if (stable) {
    updateStaticChildren(n1.children, children, parent, renderer);
  } else {
    updateDynamicChildren(n1.children, children, parent, renderer);
  }
  // Note: not reusing n1.elm, because during patching, it may be patched with another text node.
  n2.elm = n2.leading.elm;
}
function mountElement(vnode, parent, anchor, renderer) {
  const {
    sel,
    owner,
    data: {
      svg
    }
  } = vnode;
  const {
    createElement
  } = renderer;
  const namespace = isTrue(svg) ? SVG_NAMESPACE : undefined;
  const elm = vnode.elm = createElement(sel, namespace);
  linkNodeToShadow(elm, owner, renderer);
  applyStyleScoping(elm, owner, renderer);
  applyDomManual(elm, vnode);
  patchElementPropsAndAttrsAndRefs$1(null, vnode, renderer);
  insertNode(elm, parent, anchor, renderer);
  mountVNodes(vnode.children, elm, renderer, null);
}
function patchStatic(n1, n2, renderer) {
  n2.elm = n1.elm;
  // slotAssignments can only apply to the top level element, never to a static part.
  patchSlotAssignment(n1, n2, renderer);
  // The `refs` object is blown away in every re-render, so we always need to re-apply them
  patchStaticParts(n1, n2, renderer);
}
function patchElement(n1, n2, renderer) {
  const elm = n2.elm = n1.elm;
  patchElementPropsAndAttrsAndRefs$1(n1, n2, renderer);
  patchChildren(n1.children, n2.children, elm, renderer);
}
function mountStatic(vnode, parent, anchor, renderer) {
  const {
    owner
  } = vnode;
  const {
    cloneNode,
    isSyntheticShadowDefined
  } = renderer;
  const elm = vnode.elm = cloneNode(vnode.fragment, true);
  // Define the root node shadow resolver
  linkNodeToShadow(elm, owner, renderer);
  const {
    renderMode,
    shadowMode
  } = owner;
  if (isSyntheticShadowDefined) {
    // Marks this node as Static to propagate the shadow resolver. must happen after elm is assigned to the proper shadow
    if (shadowMode === 1 /* ShadowMode.Synthetic */ || renderMode === 0 /* RenderMode.Light */) {
      elm[KEY__SHADOW_STATIC] = true;
    }
  }
  // slotAssignments can only apply to the top level element, never to a static part.
  patchSlotAssignment(null, vnode, renderer);
  mountStaticParts(elm, vnode, renderer);
  insertNode(elm, parent, anchor, renderer);
}
function mountCustomElement(vnode, parent, anchor, renderer) {
  const {
    sel,
    owner,
    ctor
  } = vnode;
  const {
    createCustomElement
  } = renderer;
  /**
   * Note: if the upgradable constructor does not expect, or throw when we new it
   * with a callback as the first argument, we could implement a more advanced
   * mechanism that only passes that argument if the constructor is known to be
   * an upgradable custom element.
   */
  let vm;
  const upgradeCallback = elm => {
    // the custom element from the registry is expecting an upgrade callback
    vm = createViewModelHook(elm, vnode, renderer);
  };
  // Should never get a tag with upper case letter at this point; the compiler
  // should produce only tags with lowercase letters. However, the Java
  // compiler may generate tagnames with uppercase letters so - for backwards
  // compatibility, we lower case the tagname here.
  const normalizedTagname = sel.toLowerCase();
  const useNativeLifecycle = !lwcRuntimeFlags.DISABLE_NATIVE_CUSTOM_ELEMENT_LIFECYCLE;
  const isFormAssociated = shouldBeFormAssociated(ctor);
  const elm = createCustomElement(normalizedTagname, upgradeCallback, useNativeLifecycle, isFormAssociated);
  vnode.elm = elm;
  vnode.vm = vm;
  linkNodeToShadow(elm, owner, renderer);
  applyStyleScoping(elm, owner, renderer);
  if (vm) {
    allocateChildren(vnode, vm);
  }
  patchElementPropsAndAttrsAndRefs$1(null, vnode, renderer);
  insertNode(elm, parent, anchor, renderer);
  if (vm) {
    {
      if (!useNativeLifecycle) {
        runConnectedCallback(vm);
      }
    }
  }
  mountVNodes(vnode.children, elm, renderer, null);
  if (vm) {
    appendVM(vm);
  }
}
function patchCustomElement(n1, n2, parent, renderer) {
  // TODO [#3331]: This if branch should be removed in 246 with lwc:dynamic
  if (n1.ctor !== n2.ctor) {
    // If the constructor differs, unmount the current component and mount a new one using the new
    // constructor.
    const anchor = renderer.nextSibling(n1.elm);
    unmount(n1, parent, renderer, true);
    mountCustomElement(n2, parent, anchor, renderer);
  } else {
    // Otherwise patch the existing component with new props/attrs/etc.
    const elm = n2.elm = n1.elm;
    const vm = n2.vm = n1.vm;
    patchElementPropsAndAttrsAndRefs$1(n1, n2, renderer);
    if (!isUndefined$1(vm)) {
      // in fallback mode, the allocation will always set children to
      // empty and delegate the real allocation to the slot elements
      allocateChildren(n2, vm);
      // Solves an edge case with slotted VFragments in native shadow mode.
      //
      // During allocation, in native shadow, slotted VFragment nodes are flattened and their text delimiters are removed
      // to avoid interfering with native slot behavior. When this happens, if any of the fragments
      // were not stable, the children must go through the dynamic diffing algo.
      //
      // If the new children (n2.children) contain no VFragments, but the previous children (n1.children) were dynamic,
      // the new nodes must be marked dynamic so that all nodes are properly updated. The only indicator that the new
      // nodes need to be dynamic comes from the previous children, so we check that to determine whether we need to
      // mark the new children dynamic.
      //
      // Example:
      // n1.children: [div, VFragment('', div, null, ''), div] => [div, div, null, div]; // marked dynamic
      // n2.children: [div, null, div] => [div, null, div] // marked ???
      const {
        shadowMode,
        renderMode
      } = vm;
      if (shadowMode == 0 /* ShadowMode.Native */ && renderMode !== 0 /* RenderMode.Light */ && hasDynamicChildren(n1.children)) {
        // No-op if children has already been marked dynamic by 'allocateChildren()'.
        markAsDynamicChildren(n2.children);
      }
    }
    // in fallback mode, the children will be always empty, so, nothing
    // will happen, but in native, it does allocate the light dom
    patchChildren(n1.children, n2.children, elm, renderer);
    if (!isUndefined$1(vm)) {
      // this will probably update the shadowRoot, but only if the vm is in a dirty state
      // this is important to preserve the top to bottom synchronous rendering phase.
      rerenderVM(vm);
    }
  }
}
function mountVNodes(vnodes, parent, renderer, anchor, start = 0, end = vnodes.length) {
  for (; start < end; ++start) {
    const vnode = vnodes[start];
    if (isVNode(vnode)) {
      mount$1(vnode, parent, renderer, anchor);
    }
  }
}
function unmount(vnode, parent, renderer, doRemove = false) {
  const {
    type,
    elm,
    sel
  } = vnode;
  // When unmounting a VNode subtree not all the elements have to removed from the DOM. The
  // subtree root, is the only element worth unmounting from the subtree.
  if (doRemove && type !== 5 /* VNodeType.Fragment */) {
    // The vnode might or might not have a data.renderer associated to it
    // but the removal used here is from the owner instead.
    removeNode(elm, parent, renderer);
  }
  switch (type) {
    case 5 /* VNodeType.Fragment */:
      {
        unmountVNodes(vnode.children, parent, renderer, doRemove);
        break;
      }
    case 2 /* VNodeType.Element */:
      {
        // Slot content is removed to trigger slotchange event when removing slot.
        // Only required for synthetic shadow.
        const shouldRemoveChildren = sel === 'slot' && vnode.owner.shadowMode === 1 /* ShadowMode.Synthetic */;
        unmountVNodes(vnode.children, elm, renderer, shouldRemoveChildren);
        break;
      }
    case 3 /* VNodeType.CustomElement */:
      {
        const {
          vm
        } = vnode;
        // No need to unmount the children here, `removeVM` will take care of removing the
        // children.
        if (!isUndefined$1(vm)) {
          removeVM(vm);
        }
      }
  }
}
function unmountVNodes(vnodes, parent, renderer, doRemove = false, start = 0, end = vnodes.length) {
  for (; start < end; ++start) {
    const ch = vnodes[start];
    if (isVNode(ch)) {
      unmount(ch, parent, renderer, doRemove);
    }
  }
}
function isVNode(vnode) {
  return vnode != null;
}
function linkNodeToShadow(elm, owner, renderer) {
  const {
    renderRoot,
    renderMode,
    shadowMode
  } = owner;
  const {
    isSyntheticShadowDefined
  } = renderer;
  // TODO [#1164]: this should eventually be done by the polyfill directly
  if (isSyntheticShadowDefined) {
    if (shadowMode === 1 /* ShadowMode.Synthetic */ || renderMode === 0 /* RenderMode.Light */) {
      elm[KEY__SHADOW_RESOLVER] = renderRoot[KEY__SHADOW_RESOLVER];
    }
  }
}
function insertFragmentOrNode(vnode, parent, anchor, renderer) {
  if (isVFragment(vnode)) {
    const children = vnode.children;
    for (let i = 0; i < children.length; i += 1) {
      const child = children[i];
      if (!isNull(child)) {
        renderer.insert(child.elm, parent, anchor);
      }
    }
  } else {
    renderer.insert(vnode.elm, parent, anchor);
  }
}
function insertNode(node, parent, anchor, renderer) {
  renderer.insert(node, parent, anchor);
}
function removeNode(node, parent, renderer) {
  renderer.remove(node, parent);
}
function patchElementPropsAndAttrsAndRefs$1(oldVnode, vnode, renderer) {
  if (isNull(oldVnode)) {
    applyEventListeners(vnode, renderer);
    applyStaticClassAttribute(vnode, renderer);
    applyStaticStyleAttribute(vnode, renderer);
  }
  const {
    owner
  } = vnode;
  patchDynamicEventListeners(oldVnode, vnode, renderer, owner);
  // Attrs need to be applied to element before props IE11 will wipe out value on radio inputs if
  // value is set before type=radio.
  patchClassAttribute(oldVnode, vnode, renderer);
  patchStyleAttribute(oldVnode, vnode, renderer);
  patchAttributes(oldVnode, vnode, renderer);
  patchProps(oldVnode, vnode, renderer);
  patchSlotAssignment(oldVnode, vnode, renderer);
  // The `refs` object is blown away in every re-render, so we always need to re-apply them
  applyRefs(vnode, owner);
}
function applyStyleScoping(elm, owner, renderer) {
  const {
    getClassList
  } = renderer;
  // Set the class name for `*.scoped.css` style scoping.
  const scopeToken = getScopeTokenClass(owner, /* legacy */false);
  if (!isNull(scopeToken)) {
    if (!isValidScopeToken(scopeToken)) {
      // See W-16614556
      throw new Error('stylesheet token must be a valid string');
    }
    // TODO [#2762]: this dot notation with add is probably problematic
    // probably we should have a renderer api for just the add operation
    getClassList(elm).add(scopeToken);
  }
  // TODO [#3733]: remove support for legacy scope tokens
  if (lwcRuntimeFlags.ENABLE_LEGACY_SCOPE_TOKENS) {
    const legacyScopeToken = getScopeTokenClass(owner, /* legacy */true);
    if (!isNull(legacyScopeToken)) {
      if (!isValidScopeToken(legacyScopeToken)) {
        // See W-16614556
        throw new Error('stylesheet token must be a valid string');
      }
      // TODO [#2762]: this dot notation with add is probably problematic
      // probably we should have a renderer api for just the add operation
      getClassList(elm).add(legacyScopeToken);
    }
  }
  // Set property element for synthetic shadow DOM style scoping.
  const {
    stylesheetToken: syntheticToken
  } = owner.context;
  if (owner.shadowMode === 1 /* ShadowMode.Synthetic */) {
    if (!isUndefined$1(syntheticToken)) {
      elm.$shadowToken$ = syntheticToken;
    }
    if (lwcRuntimeFlags.ENABLE_LEGACY_SCOPE_TOKENS) {
      const legacyToken = owner.context.legacyStylesheetToken;
      if (!isUndefined$1(legacyToken)) {
        elm.$legacyShadowToken$ = legacyToken;
      }
    }
  }
}
function applyDomManual(elm, vnode) {
  const {
    owner,
    data: {
      context
    }
  } = vnode;
  if (owner.shadowMode === 1 /* ShadowMode.Synthetic */ && context?.lwc?.dom === 'manual') {
    elm.$domManual$ = true;
  }
}
function allocateChildren(vnode, vm) {
  // A component with slots will re-render because:
  // 1- There is a change of the internal state.
  // 2- There is a change on the external api (ex: slots)
  //
  // In case #1, the vnodes in the cmpSlots will be reused since they didn't changed. This routine emptied the
  // slotted children when those VCustomElement were rendered and therefore in subsequent calls to allocate children
  // in a reused VCustomElement, there won't be any slotted children.
  // For those cases, we will use the reference for allocated children stored when rendering the fresh VCustomElement.
  //
  // In case #2, we will always get a fresh VCustomElement.
  const children = vnode.aChildren || vnode.children;
  const {
    renderMode,
    shadowMode
  } = vm;
  // If any of the children being allocated are VFragments, we remove the text delimiters and flatten all immediate
  // children VFragments to avoid them interfering with default slot behavior.
  const allocatedChildren = flattenFragmentsInChildren(children);
  vnode.children = allocatedChildren;
  vm.aChildren = allocatedChildren;
  if (shadowMode === 1 /* ShadowMode.Synthetic */ || renderMode === 0 /* RenderMode.Light */) {
    // slow path
    allocateInSlot(vm, allocatedChildren, vnode.owner);
    // save the allocated children in case this vnode is reused.
    vnode.aChildren = allocatedChildren;
    // every child vnode is now allocated, and the host should receive none directly, it receives them via the shadow!
    vnode.children = EmptyArray;
  }
}
/**
 * Flattens the contents of all VFragments in an array of VNodes, removes the text delimiters on those VFragments, and
 * marks the resulting children array as dynamic. Uses a stack (array) to iteratively traverse the nested VFragments
 * and avoid the perf overhead of creating/destroying throwaway arrays/objects in a recursive approach.
 *
 * With the delimiters removed, the contents are marked dynamic so they are diffed correctly.
 *
 * This function is used for slotted VFragments to avoid the text delimiters interfering with slotting functionality.
 * @param children
 */
function flattenFragmentsInChildren(children) {
  const flattenedChildren = [];
  // Initialize our stack with the direct children of the custom component and check whether we have a VFragment.
  // If no VFragment is found in children, we don't need to traverse anything or mark the children dynamic and can return early.
  const nodeStack = [];
  let fragmentFound = false;
  for (let i = children.length - 1; i > -1; i -= 1) {
    const child = children[i];
    ArrayPush$1.call(nodeStack, child);
    fragmentFound = fragmentFound || !!(child && isVFragment(child));
  }
  if (!fragmentFound) {
    return children;
  }
  let currentNode;
  while (!isUndefined$1(currentNode = ArrayPop.call(nodeStack))) {
    if (!isNull(currentNode) && isVFragment(currentNode)) {
      const fChildren = currentNode.children;
      // Ignore the start and end text node delimiters
      for (let i = fChildren.length - 2; i > 0; i -= 1) {
        ArrayPush$1.call(nodeStack, fChildren[i]);
      }
    } else {
      ArrayPush$1.call(flattenedChildren, currentNode);
    }
  }
  // We always mark the children as dynamic because nothing generates stable VFragments yet.
  // If/when stable VFragments are generated by the compiler, this code should be updated to
  // not mark dynamic if all flattened VFragments were stable.
  markAsDynamicChildren(flattenedChildren);
  return flattenedChildren;
}
function createViewModelHook(elm, vnode, renderer) {
  let vm = getAssociatedVMIfPresent(elm);
  // There is a possibility that a custom element is registered under tagName, in which case, the
  // initialization is already carry on, and there is nothing else to do here since this hook is
  // called right after invoking `document.createElement`.
  if (!isUndefined$1(vm)) {
    return vm;
  }
  const {
    sel,
    mode,
    ctor,
    owner
  } = vnode;
  vm = createVM(elm, ctor, renderer, {
    mode,
    owner,
    tagName: sel
  });
  return vm;
}
function allocateInSlot(vm, children, owner) {
  const {
    cmpSlots: {
      slotAssignments: oldSlotsMapping
    }
  } = vm;
  const cmpSlotsMapping = create(null);
  // Collect all slots into cmpSlotsMapping
  for (let i = 0, len = children.length; i < len; i += 1) {
    const vnode = children[i];
    if (isNull(vnode)) {
      continue;
    }
    let slotName = '';
    if (isVBaseElement(vnode) || isVStatic(vnode)) {
      slotName = vnode.slotAssignment ?? '';
    } else if (isVScopedSlotFragment(vnode)) {
      slotName = vnode.slotName;
    }
    // Can't use toString here because Symbol(1).toString() is 'Symbol(1)'
    // but elm.setAttribute('slot', Symbol(1)) is an error.
    // the following line also throws same error for symbols
    // Similar for Object.create(null)
    // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
    const normalizedSlotName = '' + slotName;
    const vnodes = cmpSlotsMapping[normalizedSlotName] = cmpSlotsMapping[normalizedSlotName] || [];
    ArrayPush$1.call(vnodes, vnode);
  }
  vm.cmpSlots = {
    owner,
    slotAssignments: cmpSlotsMapping
  };
  if (isFalse(vm.isDirty)) {
    // We need to determine if the old allocation is really different from the new one
    // and mark the vm as dirty
    const oldKeys = keys(oldSlotsMapping);
    if (oldKeys.length !== keys(cmpSlotsMapping).length) {
      markComponentAsDirty(vm);
      return;
    }
    for (let i = 0, len = oldKeys.length; i < len; i += 1) {
      const key = oldKeys[i];
      if (isUndefined$1(cmpSlotsMapping[key]) || oldSlotsMapping[key].length !== cmpSlotsMapping[key].length) {
        markComponentAsDirty(vm);
        return;
      }
      const oldVNodes = oldSlotsMapping[key];
      const vnodes = cmpSlotsMapping[key];
      for (let j = 0, a = cmpSlotsMapping[key].length; j < a; j += 1) {
        if (oldVNodes[j] !== vnodes[j]) {
          markComponentAsDirty(vm);
          return;
        }
      }
    }
  }
}
const DynamicChildren = new WeakSet();
// dynamic children means it was either generated by an iteration in a template
// or part of an unstable fragment, and will require a more complex diffing algo.
function markAsDynamicChildren(children) {
  DynamicChildren.add(children);
}
function hasDynamicChildren(children) {
  return DynamicChildren.has(children);
}
function createKeyToOldIdx(children, beginIdx, endIdx) {
  const map = {};
  for (let j = beginIdx; j <= endIdx; ++j) {
    const ch = children[j];
    if (isVNode(ch)) {
      const {
        key
      } = ch;
      if (key !== undefined) {
        map[key] = j;
      }
    }
  }
  return map;
}
function updateDynamicChildren(oldCh, newCh, parent, renderer) {
  let oldStartIdx = 0;
  let newStartIdx = 0;
  let oldEndIdx = oldCh.length - 1;
  let oldStartVnode = oldCh[0];
  let oldEndVnode = oldCh[oldEndIdx];
  const newChEnd = newCh.length - 1;
  let newEndIdx = newChEnd;
  let newStartVnode = newCh[0];
  let newEndVnode = newCh[newEndIdx];
  let oldKeyToIdx;
  let idxInOld;
  let elmToMove;
  let before;
  let clonedOldCh = false;
  while (oldStartIdx <= oldEndIdx && newStartIdx <= newEndIdx) {
    if (!isVNode(oldStartVnode)) {
      oldStartVnode = oldCh[++oldStartIdx]; // Vnode might have been moved left
    } else if (!isVNode(oldEndVnode)) {
      oldEndVnode = oldCh[--oldEndIdx];
    } else if (!isVNode(newStartVnode)) {
      newStartVnode = newCh[++newStartIdx];
    } else if (!isVNode(newEndVnode)) {
      newEndVnode = newCh[--newEndIdx];
    } else if (isSameVnode(oldStartVnode, newStartVnode)) {
      patch(oldStartVnode, newStartVnode, parent, renderer);
      oldStartVnode = oldCh[++oldStartIdx];
      newStartVnode = newCh[++newStartIdx];
    } else if (isSameVnode(oldEndVnode, newEndVnode)) {
      patch(oldEndVnode, newEndVnode, parent, renderer);
      oldEndVnode = oldCh[--oldEndIdx];
      newEndVnode = newCh[--newEndIdx];
    } else if (isSameVnode(oldStartVnode, newEndVnode)) {
      // Vnode moved right
      patch(oldStartVnode, newEndVnode, parent, renderer);
      // In the case of fragments, the `elm` property of a vfragment points to the leading
      // anchor. To determine the next sibling of the whole fragment, we need to use the
      // trailing anchor as the argument to nextSibling():
      // [..., [leading, ...content, trailing], nextSibling, ...]
      let anchor;
      if (isVFragment(oldEndVnode)) {
        anchor = renderer.nextSibling(oldEndVnode.trailing.elm);
      } else {
        anchor = renderer.nextSibling(oldEndVnode.elm);
      }
      insertFragmentOrNode(oldStartVnode, parent, anchor, renderer);
      oldStartVnode = oldCh[++oldStartIdx];
      newEndVnode = newCh[--newEndIdx];
    } else if (isSameVnode(oldEndVnode, newStartVnode)) {
      // Vnode moved left
      patch(oldEndVnode, newStartVnode, parent, renderer);
      insertFragmentOrNode(newStartVnode, parent, oldStartVnode.elm, renderer);
      oldEndVnode = oldCh[--oldEndIdx];
      newStartVnode = newCh[++newStartIdx];
    } else {
      if (oldKeyToIdx === undefined) {
        oldKeyToIdx = createKeyToOldIdx(oldCh, oldStartIdx, oldEndIdx);
      }
      idxInOld = oldKeyToIdx[newStartVnode.key];
      if (isUndefined$1(idxInOld)) {
        // New element
        mount$1(newStartVnode, parent, renderer, oldStartVnode.elm);
        newStartVnode = newCh[++newStartIdx];
      } else {
        elmToMove = oldCh[idxInOld];
        if (isVNode(elmToMove)) {
          if (elmToMove.sel !== newStartVnode.sel) {
            // New element
            mount$1(newStartVnode, parent, renderer, oldStartVnode.elm);
          } else {
            patch(elmToMove, newStartVnode, parent, renderer);
            // Delete the old child, but copy the array since it is read-only.
            // The `oldCh` will be GC'ed after `updateDynamicChildren` is complete,
            // so we only care about the `oldCh` object inside this function.
            // To avoid cloning over and over again, we check `clonedOldCh`
            // and only clone once.
            if (!clonedOldCh) {
              clonedOldCh = true;
              oldCh = [...oldCh];
            }
            // We've already cloned at least once, so it's no longer read-only
            oldCh[idxInOld] = undefined;
            insertFragmentOrNode(elmToMove, parent, oldStartVnode.elm, renderer);
          }
        }
        newStartVnode = newCh[++newStartIdx];
      }
    }
  }
  if (oldStartIdx <= oldEndIdx || newStartIdx <= newEndIdx) {
    if (oldStartIdx > oldEndIdx) {
      // There's some cases in which the sub array of vnodes to be inserted is followed by null(s) and an
      // already processed vnode, in such cases the vnodes to be inserted should be before that processed vnode.
      let i = newEndIdx;
      let n;
      do {
        n = newCh[++i];
      } while (!isVNode(n) && i < newChEnd);
      before = isVNode(n) ? n.elm : null;
      mountVNodes(newCh, parent, renderer, before, newStartIdx, newEndIdx + 1);
    } else {
      unmountVNodes(oldCh, parent, renderer, true, oldStartIdx, oldEndIdx + 1);
    }
  }
}
function updateStaticChildren(c1, c2, parent, renderer) {
  const c1Length = c1.length;
  const c2Length = c2.length;
  if (c1Length === 0) {
    // the old list is empty, we can directly insert anything new
    mountVNodes(c2, parent, renderer, null);
    return;
  }
  if (c2Length === 0) {
    // the old list is nonempty and the new list is empty so we can directly remove all old nodes
    // this is the case in which the dynamic children of an if-directive should be removed
    unmountVNodes(c1, parent, renderer, true);
    return;
  }
  // if the old list is not empty, the new list MUST have the same
  // amount of nodes, that's why we call this static children
  let anchor = null;
  for (let i = c2Length - 1; i >= 0; i -= 1) {
    const n1 = c1[i];
    const n2 = c2[i];
    if (n2 !== n1) {
      if (isVNode(n1)) {
        if (isVNode(n2)) {
          if (isSameVnode(n1, n2)) {
            // both vnodes are equivalent, and we just need to patch them
            patch(n1, n2, parent, renderer);
            anchor = n2.elm;
          } else {
            // removing the old vnode since the new one is different
            unmount(n1, parent, renderer, true);
            mount$1(n2, parent, renderer, anchor);
            anchor = n2.elm;
          }
        } else {
          // removing the old vnode since the new one is null
          unmount(n1, parent, renderer, true);
        }
      } else if (isVNode(n2)) {
        mount$1(n2, parent, renderer, anchor);
        anchor = n2.elm;
      }
    }
  }
}

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const SymbolIterator = Symbol.iterator;
function addVNodeToChildLWC(vnode) {
  ArrayPush$1.call(getVMBeingRendered().velements, vnode);
}
// [s]tatic [p]art
function sp(partId, data, text) {
  // Static part will always have either text or data, it's guaranteed by the compiler.
  const type = isNull(text) ? 1 /* VStaticPartType.Element */ : 0 /* VStaticPartType.Text */;
  return {
    type,
    partId,
    data,
    text,
    elm: undefined // elm is defined later
  };
}
// [s]coped [s]lot [f]actory
function ssf(slotName, factory) {
  return {
    type: 6 /* VNodeType.ScopedSlotFragment */,
    factory,
    owner: getVMBeingRendered(),
    elm: undefined,
    sel: '__scoped_slot_fragment__',
    key: undefined,
    slotName
  };
}
// [st]atic node
function st(fragmentFactory, key, parts) {
  const owner = getVMBeingRendered();
  const fragment = fragmentFactory(parts);
  const vnode = {
    type: 4 /* VNodeType.Static */,
    sel: '__static__',
    key,
    elm: undefined,
    fragment,
    owner,
    parts,
    slotAssignment: undefined
  };
  return vnode;
}
// [fr]agment node
function fr(key, children, stable) {
  const owner = getVMBeingRendered();
  const useCommentNodes = isAPIFeatureEnabled(5 /* APIFeature.USE_COMMENTS_FOR_FRAGMENT_BOOKENDS */, owner.apiVersion);
  const leading = useCommentNodes ? co('') : t('');
  const trailing = useCommentNodes ? co('') : t('');
  return {
    type: 5 /* VNodeType.Fragment */,
    sel: '__fragment__',
    key,
    elm: undefined,
    children: [leading, ...children, trailing],
    stable,
    owner,
    leading,
    trailing
  };
}
// [h]tml node
function h(sel, data, children = EmptyArray) {
  const vmBeingRendered = getVMBeingRendered();
  const {
    key,
    slotAssignment
  } = data;
  const vnode = {
    type: 2 /* VNodeType.Element */,
    sel,
    data,
    children,
    elm: undefined,
    key,
    owner: vmBeingRendered,
    slotAssignment
  };
  return vnode;
}
// [t]ab[i]ndex function
function ti(value) {
  // if value is greater than 0, we normalize to 0
  // If value is an invalid tabIndex value (null, undefined, string, etc), we let that value pass through
  // If value is less than -1, we don't care
  const shouldNormalize = value > 0 && !(isTrue(value) || isFalse(value));
  return shouldNormalize ? 0 : value;
}
// [s]lot element node
function s(slotName, data, children, slotset) {
  const vmBeingRendered = getVMBeingRendered();
  const {
    renderMode,
    apiVersion
  } = vmBeingRendered;
  if (!isUndefined$1(slotset) && !isUndefined$1(slotset.slotAssignments) && !isUndefined$1(slotset.slotAssignments[slotName]) && slotset.slotAssignments[slotName].length !== 0) {
    const newChildren = [];
    const slotAssignments = slotset.slotAssignments[slotName];
    for (let i = 0; i < slotAssignments.length; i++) {
      const vnode = slotAssignments[i];
      if (!isNull(vnode)) {
        const assignedNodeIsScopedSlot = isVScopedSlotFragment(vnode);
        // The only sniff test for a scoped <slot> element is the presence of `slotData`
        const isScopedSlotElement = !isUndefined$1(data.slotData);
        // Check if slot types of parent and child are matching
        if (assignedNodeIsScopedSlot !== isScopedSlotElement) {
          // Ignore slot content from parent
          continue;
        }
        // If the passed slot content is factory, evaluate it and add the produced vnodes
        if (assignedNodeIsScopedSlot) {
          // Evaluate in the scope of the slot content's owner
          // if a slotset is provided, there will always be an owner. The only case where owner is
          // undefined is for root components, but root components cannot accept slotted content
          setVMBeingRendered(slotset.owner);
          try {
            // The factory function is a template snippet from the slot set owner's template,
            // hence switch over to the slot set owner's template reactive observer
            const {
              tro
            } = slotset.owner;
            tro.observe(() => {
              ArrayPush$1.call(newChildren, vnode.factory(data.slotData, data.key));
            });
          } finally {
            setVMBeingRendered(vmBeingRendered);
          }
        } else {
          // This block is for standard slots (non-scoped slots)
          let clonedVNode;
          if (renderMode === 0 /* RenderMode.Light */ && isAPIFeatureEnabled(6 /* APIFeature.USE_LIGHT_DOM_SLOT_FORWARDING */, apiVersion) && (isVBaseElement(vnode) || isVStatic(vnode)) && vnode.slotAssignment !== data.slotAssignment) {
            // When the light DOM slot assignment (slot attribute) changes, we can't use the same reference
            // to the vnode because the current way the diffing algo works, it will replace the original
            // reference to the host element with a new one. This means the new element will be mounted and
            // immediately unmounted. Creating a copy of the vnode preserves a reference to the previous
            // host element.
            clonedVNode = {
              ...vnode,
              slotAssignment: data.slotAssignment
            };
            // For disconnectedCallback to work correctly in synthetic lifecycle mode, we need to link the
            // current VM's velements to the clone, so that when the VM unmounts, the clone also unmounts.
            // Note this only applies to VCustomElements, since those are the elements that we manually need
            // to call disconnectedCallback for, when running in synthetic lifecycle mode.
            //
            // You might think it would make more sense to add the clonedVNode to the same velements array
            // as the original vnode's VM (i.e. `vnode.owner.velements`) rather than the current VM (i.e.
            // `vmBeingRendered.velements`), but this actually might not trigger disconnectedCallback
            // in synthetic lifecycle mode. The reason for this is that a reactivity change may cause
            // the slottable component to unmount, but _not_ the slotter component (see issue #4446).
            //
            // If this occurs, then the slottable component (i.e .this component we are rendering right
            // now) is the one that needs to own the clone. Whereas if a reactivity change higher in the
            // tree causes the slotter to unmount, then the slottable will also unmount. So using the
            // current VM works either way.
            if (isVCustomElement(vnode)) {
              addVNodeToChildLWC(clonedVNode);
            }
          }
          // If the slot content is standard type, the content is static, no additional
          // processing needed on the vnode
          ArrayPush$1.call(newChildren, clonedVNode ?? vnode);
        }
      }
    }
    children = newChildren;
  }
  const {
    shadowMode
  } = vmBeingRendered;
  if (renderMode === 0 /* RenderMode.Light */) {
    // light DOM slots - backwards-compatible behavior uses flattening, new behavior uses fragments
    if (isAPIFeatureEnabled(2 /* APIFeature.USE_FRAGMENTS_FOR_LIGHT_DOM_SLOTS */, apiVersion)) {
      return fr(data.key, children, 0);
    } else {
      sc(children);
      return children;
    }
  }
  if (shadowMode === 1 /* ShadowMode.Synthetic */) {
    // TODO [#1276]: compiler should give us some sort of indicator when a vnodes collection is dynamic
    sc(children);
  }
  return h('slot', data, children);
}
// [c]ustom element node
function c(sel, Ctor, data, children = EmptyArray) {
  const vmBeingRendered = getVMBeingRendered();
  const {
    key,
    slotAssignment
  } = data;
  const vnode = {
    type: 3 /* VNodeType.CustomElement */,
    sel,
    data,
    children,
    elm: undefined,
    key,
    slotAssignment,
    ctor: Ctor,
    owner: vmBeingRendered,
    mode: 'open',
    // TODO [#1294]: this should be defined in Ctor
    aChildren: undefined,
    vm: undefined
  };
  addVNodeToChildLWC(vnode);
  return vnode;
}
// [i]terable node
function i(iterable, factory) {
  const list = [];
  // TODO [#1276]: compiler should give us some sort of indicator when a vnodes collection is dynamic
  sc(list);
  if (isUndefined$1(iterable) || isNull(iterable)) {
    return list;
  }
  const iterator = iterable[SymbolIterator]();
  let next = iterator.next();
  let j = 0;
  let {
    value,
    done: last
  } = next;
  while (last === false) {
    // implementing a look-back-approach because we need to know if the element is the last
    next = iterator.next();
    last = next.done;
    // template factory logic based on the previous collected value
    const vnode = factory(value, j, j === 0, last === true);
    if (isArray$1(vnode)) {
      ArrayPush$1.apply(list, vnode);
    } else {
      // `isArray` doesn't narrow this block properly...
      ArrayPush$1.call(list, vnode);
    }
    // preparing next value
    j += 1;
    value = next.value;
  }
  return list;
}
/**
 * [f]lattening
 * @param items
 */
function f(items) {
  const len = items.length;
  const flattened = [];
  // TODO [#1276]: compiler should give us some sort of indicator when a vnodes collection is dynamic
  sc(flattened);
  for (let j = 0; j < len; j += 1) {
    const item = items[j];
    if (isArray$1(item)) {
      ArrayPush$1.apply(flattened, item);
    } else {
      // `isArray` doesn't narrow this block properly...
      ArrayPush$1.call(flattened, item);
    }
  }
  return flattened;
}
// [t]ext node
function t(text) {
  return {
    type: 0 /* VNodeType.Text */,
    sel: '__text__',
    text,
    elm: undefined,
    key: undefined,
    owner: getVMBeingRendered()
  };
}
// [co]mment node
function co(text) {
  return {
    type: 1 /* VNodeType.Comment */,
    sel: '__comment__',
    text,
    elm: undefined,
    key: undefined,
    owner: getVMBeingRendered()
  };
}
// [d]ynamic text
function d(value) {
  return value == null ? '' : String(value);
}
// [b]ind function
function b(fn) {
  const vmBeingRendered = getVMBeingRendered();
  if (isNull(vmBeingRendered)) {
    throw new Error();
  }
  const vm = vmBeingRendered;
  return function (event) {
    invokeEventListener(vm, fn, vm.component, event);
  };
}
// [k]ey function
function k(compilerKey, obj) {
  switch (typeof obj) {
    case 'number':
    case 'string':
      return compilerKey + ':' + obj;
  }
}
// [g]lobal [id] function
function gid(id) {
  const vmBeingRendered = getVMBeingRendered();
  if (isUndefined$1(id) || id === '') {
    return id;
  }
  // We remove attributes when they are assigned a value of null
  if (isNull(id)) {
    return null;
  }
  const {
    idx,
    shadowMode
  } = vmBeingRendered;
  if (shadowMode === 1 /* ShadowMode.Synthetic */) {
    return StringReplace.call(id, /\S+/g, id => `${id}-${idx}`);
  }
  return id;
}
// [f]ragment [id] function
function fid(url) {
  const vmBeingRendered = getVMBeingRendered();
  if (isUndefined$1(url) || url === '') {
    return url;
  }
  // We remove attributes when they are assigned a value of null
  if (isNull(url)) {
    return null;
  }
  const {
    idx,
    shadowMode
  } = vmBeingRendered;
  // Apply transformation only for fragment-only-urls, and only in shadow DOM
  if (shadowMode === 1 /* ShadowMode.Synthetic */ && /^#/.test(url)) {
    return `${url}-${idx}`;
  }
  return url;
}
/**
 * [ddc] - create a (deprecated) dynamic component via `<x-foo lwc:dynamic={Ctor}>`
 *
 * TODO [#3331]: remove usage of lwc:dynamic in 246
 * @param sel
 * @param Ctor
 * @param data
 * @param children
 */
function ddc(sel, Ctor, data, children = EmptyArray) {
  // null or undefined values should produce a null value in the VNodes
  if (isNull(Ctor) || isUndefined$1(Ctor)) {
    return null;
  }
  if (!isComponentConstructor(Ctor)) {
    throw new Error(`Invalid LWC Constructor ${toString(Ctor)} for custom element <${sel}>.`);
  }
  return c(sel, Ctor, data, children);
}
/**
 * [dc] - create a dynamic component via `<lwc:component lwc:is={Ctor}>`
 * @param Ctor
 * @param data
 * @param children
 */
function dc(Ctor, data, children = EmptyArray) {
  // Null or undefined values should produce a null value in the VNodes.
  // This is the only value at compile time as the constructor will not be known.
  if (isNull(Ctor) || isUndefined$1(Ctor)) {
    return null;
  }
  if (!isComponentConstructor(Ctor)) {
    throw new Error(`Invalid constructor: "${toString(Ctor)}" is not a LightningElement constructor.`);
  }
  // Look up the dynamic component's name at runtime once the constructor is available.
  // This information is only known at runtime and is stored as part of registerComponent.
  const sel = getComponentRegisteredName(Ctor);
  if (isUndefined$1(sel) || sel === '') {
    throw new Error(`Invalid LWC constructor ${toString(Ctor)} does not have a registered name`);
  }
  return c(sel, Ctor, data, children);
}
/**
 * slow children collection marking mechanism. this API allows the compiler to signal
 * to the engine that a particular collection of children must be diffed using the slow
 * algo based on keys due to the nature of the list. E.g.:
 *
 * - slot element's children: the content of the slot has to be dynamic when in synthetic
 * shadow mode because the `vnode.children` might be the slotted
 * content vs default content, in which case the size and the
 * keys are not matching.
 * - children that contain dynamic components
 * - children that are produced by iteration
 * @param vnodes
 */
function sc(vnodes) {
  // We have to mark the vnodes collection as dynamic so we can later on
  // choose to use the snabbdom virtual dom diffing algo instead of our
  // static dummy algo.
  markAsDynamicChildren(vnodes);
  return vnodes;
}
// [s]anitize [h]tml [c]ontent
function shc(content) {
  const sanitizedString = sanitizeHtmlContent();
  return createSanitizedHtmlContent(sanitizedString);
}
const ncls = normalizeClass;
const api = freeze({
  s,
  h,
  c,
  i,
  f,
  t,
  d,
  b,
  k,
  co,
  dc,
  fr,
  ti,
  st,
  gid,
  fid,
  shc,
  ssf,
  ddc,
  sp,
  ncls
});

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
// HAS_SCOPED_STYLE | SHADOW_MODE_SYNTHETIC = 3
const MAX_CACHE_KEY = 3;
// Mapping of cacheKeys to `string[]` (assumed to come from a tagged template literal) to an Element.
// Note that every unique tagged template literal will have a unique `string[]`. So by using `string[]`
// as the WeakMap key, we effectively associate each Element with a unique tagged template literal.
// See: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Template_literals#tagged_templates
// Also note that this array only needs to be large enough to account for the maximum possible cache key
const fragmentCache = ArrayFrom({
  length: MAX_CACHE_KEY + 1
}, () => new WeakMap());
function getFromFragmentCache(cacheKey, strings) {
  return fragmentCache[cacheKey].get(strings);
}
function setInFragmentCache(cacheKey, strings, element) {
  fragmentCache[cacheKey].set(strings, element);
}

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
let isUpdatingTemplate = false;
let vmBeingRendered = null;
function getVMBeingRendered() {
  return vmBeingRendered;
}
function setVMBeingRendered(vm) {
  vmBeingRendered = vm;
}
function validateSlots(vm) {
  assertNotProd(); // this method should never leak to prod
  const {
    cmpSlots
  } = vm;
  for (const slotName in cmpSlots.slotAssignments) {
    assert.isTrue(isArray$1(cmpSlots.slotAssignments[slotName]), `Slots can only be set to an array, instead received ${toString(cmpSlots.slotAssignments[slotName])} for slot "${slotName}" in ${vm}.`);
  }
}
function checkHasMatchingRenderMode(template, vm) {
  // don't validate in prod environments where reporting is disabled
  {
    return;
  }
}
const browserExpressionSerializer = (partToken, classAttrToken) => {
  // This will insert the scoped style token as a static class attribute in the fragment
  // bypassing the need to call applyStyleScoping when mounting static parts.
  const type = StringCharAt.call(partToken, 0);
  switch (type) {
    case "c" /* STATIC_PART_TOKEN_ID.CLASS */:
      return classAttrToken;
    case "t" /* STATIC_PART_TOKEN_ID.TEXT */:
      // Using a single space here gives us a single empty text node
      return ' ';
    default:
      return '';
  }
};
// This function serializes the expressions generated by static content optimization.
// Currently this is only needed for SSR.
// TODO [#4078]: Split the implementation between @lwc/engine-dom and @lwc/engine-server
function buildSerializeExpressionFn(parts) {
  {
    return browserExpressionSerializer;
  }
}
function buildParseFragmentFn(createFragmentFn) {
  return function parseFragment(strings, ...keys) {
    return function applyFragmentParts(parts) {
      const {
        context: {
          hasScopedStyles,
          stylesheetToken,
          legacyStylesheetToken
        },
        shadowMode,
        renderer
      } = getVMBeingRendered();
      const hasStyleToken = !isUndefined$1(stylesheetToken);
      const isSyntheticShadow = shadowMode === 1 /* ShadowMode.Synthetic */;
      const hasLegacyToken = lwcRuntimeFlags.ENABLE_LEGACY_SCOPE_TOKENS && !isUndefined$1(legacyStylesheetToken);
      let cacheKey = 0;
      if (hasStyleToken && hasScopedStyles) {
        cacheKey |= 1 /* FragmentCacheKey.HAS_SCOPED_STYLE */;
      }
      if (hasStyleToken && isSyntheticShadow) {
        cacheKey |= 2 /* FragmentCacheKey.SHADOW_MODE_SYNTHETIC */;
      }
      // Cache is only here to prevent calling innerHTML multiple times which doesn't happen on the server.
      {
        // Disable this on the server to prevent cache poisoning when expressions are used.
        const cached = getFromFragmentCache(cacheKey, strings);
        if (!isUndefined$1(cached)) {
          return cached;
        }
      }
      // See W-16614556
      // TODO [#2826]: freeze the template object
      if (hasStyleToken && !isValidScopeToken(stylesheetToken) || hasLegacyToken && !isValidScopeToken(legacyStylesheetToken)) {
        throw new Error('stylesheet token must be a valid string');
      }
      // If legacy stylesheet tokens are required, then add them to the rendered string
      const stylesheetTokenToRender = stylesheetToken + (hasLegacyToken ? ` ${legacyStylesheetToken}` : '');
      const classToken = hasScopedStyles && hasStyleToken ? ' ' + stylesheetTokenToRender : '';
      const classAttrToken = hasScopedStyles && hasStyleToken ? ` class="${stylesheetTokenToRender}"` : '';
      const attrToken = hasStyleToken && isSyntheticShadow ? ' ' + stylesheetTokenToRender : '';
      // In the browser, we provide the entire class attribute as a perf optimization to avoid applying it on mount.
      // The remaining class expression will be applied when the static parts are mounted.
      // In SSR, the entire class attribute (expression included) is assembled along with the fragment.
      // This is why in the browser we provide the entire class attribute and in SSR we only provide the class token.
      const exprClassToken = classAttrToken;
      // TODO [#3624]: The implementation of this function should be specific to @lwc/engine-dom and @lwc/engine-server.
      // Find a way to split this in a future refactor.
      const serializeExpression = buildSerializeExpressionFn();
      let htmlFragment = '';
      for (let i = 0, n = keys.length; i < n; i++) {
        switch (keys[i]) {
          case 0:
            // styleToken in existing class attr
            htmlFragment += strings[i] + classToken;
            break;
          case 1:
            // styleToken for added class attr
            htmlFragment += strings[i] + classAttrToken;
            break;
          case 2:
            // styleToken as attr
            htmlFragment += strings[i] + attrToken;
            break;
          case 3:
            // ${1}${2}
            htmlFragment += strings[i] + classAttrToken + attrToken;
            break;
          default:
            // expressions ${partId:attributeName/textId}
            htmlFragment += strings[i] + serializeExpression(keys[i], exprClassToken);
            break;
        }
      }
      htmlFragment += strings[strings.length - 1];
      const element = createFragmentFn(htmlFragment, renderer);
      // Cache is only here to prevent calling innerHTML multiple times which doesn't happen on the server.
      {
        setInFragmentCache(cacheKey, strings, element);
      }
      return element;
    };
  };
}
// Note: at the moment this code executes, we don't have a renderer yet.
const parseFragment = buildParseFragmentFn((html, renderer) => {
  const {
    createFragment
  } = renderer;
  return createFragment(html);
});
function evaluateTemplate(vm, html) {
  const isUpdatingTemplateInception = isUpdatingTemplate;
  const vmOfTemplateBeingUpdatedInception = vmBeingRendered;
  let vnodes = [];
  runWithBoundaryProtection(vm, vm.owner, () => {
    // pre
    vmBeingRendered = vm;
  }, () => {
    // job
    const {
      component,
      context,
      cmpSlots,
      cmpTemplate,
      tro
    } = vm;
    tro.observe(() => {
      // Reset the cache memoizer for template when needed.
      if (html !== cmpTemplate) {
        // Check that the template was built by the compiler.
        if (!isTemplateRegistered(html)) {
          throw new TypeError(`Invalid template returned by the render() method on ${vm.tagName}. It must return an imported template (e.g.: \`import html from "./${vm.def.name}.html"\`), instead, it has returned: ${toString(html)}.`);
        }
        checkHasMatchingRenderMode(html, vm);
        // Perf opt: do not reset the shadow root during the first rendering (there is
        // nothing to reset).
        if (!isNull(cmpTemplate)) {
          // It is important to reset the content to avoid reusing similar elements
          // generated from a different template, because they could have similar IDs,
          // and snabbdom just rely on the IDs.
          resetComponentRoot(vm);
        }
        vm.cmpTemplate = html;
        // Create a brand new template cache for the swapped templated.
        context.tplCache = create(null);
        // Set the computeHasScopedStyles property in the context, to avoid recomputing it repeatedly.
        context.hasScopedStyles = computeHasScopedStyles(html, vm);
        // Update the scoping token on the host element.
        updateStylesheetToken(vm, html, /* legacy */false);
        if (lwcRuntimeFlags.ENABLE_LEGACY_SCOPE_TOKENS) {
          updateStylesheetToken(vm, html, /* legacy */true);
        }
        // Evaluate, create stylesheet and cache the produced VNode for future
        // re-rendering.
        const stylesheetsContent = getStylesheetsContent(vm, html);
        context.styleVNodes = stylesheetsContent.length === 0 ? null : createStylesheet(vm, stylesheetsContent);
      }
      if ("production" !== 'production') ;
      // right before producing the vnodes, we clear up all internal references
      // to custom elements from the template.
      vm.velements = [];
      // Set the global flag that template is being updated
      isUpdatingTemplate = true;
      vnodes = html.call(undefined, api, component, cmpSlots, context.tplCache);
      const {
        styleVNodes
      } = context;
      if (!isNull(styleVNodes)) {
        // It's important here not to mutate the underlying `vnodes` returned from `html.call()`.
        // The reason for this is because, due to the static content optimization, the vnodes array
        // may be a static array shared across multiple component instances. E.g. this occurs in the
        // case of an empty `<template></template>` in a `component.html` file, due to the underlying
        // children being `[]` (no children). If we append the `<style>` vnode to this array, then the same
        // array will be reused for every component instance, i.e. whenever `tmpl()` is called.
        vnodes = [...styleVNodes, ...vnodes];
      }
    });
  }, () => {
    // post
    isUpdatingTemplate = isUpdatingTemplateInception;
    vmBeingRendered = vmOfTemplateBeingUpdatedInception;
  });
  return vnodes;
}
function computeHasScopedStylesInStylesheets(stylesheets) {
  if (hasStyles(stylesheets)) {
    for (let i = 0; i < stylesheets.length; i++) {
      if (isTrue(stylesheets[i][KEY__SCOPED_CSS])) {
        return true;
      }
    }
  }
  return false;
}
function computeHasScopedStyles(template, vm) {
  const {
    stylesheets
  } = template;
  const vmStylesheets = !isUndefined$1(vm) ? vm.stylesheets : null;
  return computeHasScopedStylesInStylesheets(stylesheets) || computeHasScopedStylesInStylesheets(vmStylesheets);
}
function hasStyles(stylesheets) {
  return !isUndefined$1(stylesheets) && !isNull(stylesheets) && stylesheets.length > 0;
}
let vmBeingConstructed = null;
function isBeingConstructed(vm) {
  return vmBeingConstructed === vm;
}
function invokeComponentCallback(vm, fn, args) {
  const {
    component,
    callHook,
    owner
  } = vm;
  runWithBoundaryProtection(vm, owner, noop, () => {
    callHook(component, fn, args);
  }, noop);
}
function invokeComponentConstructor(vm, Ctor) {
  const vmBeingConstructedInception = vmBeingConstructed;
  let error;
  vmBeingConstructed = vm;
  /**
   * Constructors don't need to be wrapped with a boundary because for root elements
   * it should throw, while elements from template are already wrapped by a boundary
   * associated to the diffing algo.
   */
  try {
    // job
    const result = new Ctor();
    // When strict, reject when the constructor returns a *native* HTMLElement — that is,
    // result instanceof HTMLElement.
    const useStrictValidation = !lwcRuntimeFlags.DISABLE_STRICT_VALIDATION && true;
    const isMismatchedConstructor = vmBeingConstructed.component !== result;
    const isInvalidConstructor = isMismatchedConstructor || useStrictValidation && result instanceof HTMLElement;
    if (isInvalidConstructor) {
      throw new TypeError('Invalid component constructor, the class should extend LightningElement.');
    }
  } catch (e) {
    error = Object(e);
  } finally {
    vmBeingConstructed = vmBeingConstructedInception;
    if (!isUndefined$1(error)) {
      addErrorComponentStack(vm, error);
      // re-throwing the original error annotated after restoring the context
      throw error; // eslint-disable-line no-unsafe-finally
    }
  }
}
function invokeComponentRenderMethod(vm) {
  const {
    def: {
      render
    },
    callHook,
    component,
    owner
  } = vm;
  const vmBeingRenderedInception = getVMBeingRendered();
  let html;
  let renderInvocationSuccessful = false;
  runWithBoundaryProtection(vm, owner, () => {
    setVMBeingRendered(vm);
  }, () => {
    // job
    vm.tro.observe(() => {
      html = callHook(component, render);
      renderInvocationSuccessful = true;
    });
  }, () => {
    setVMBeingRendered(vmBeingRenderedInception);
  });
  // If render() invocation failed, process errorCallback in boundary and return an empty template
  return renderInvocationSuccessful ? evaluateTemplate(vm, html) : [];
}
function invokeEventListener(vm, fn, thisValue, event) {
  const {
    callHook,
    owner
  } = vm;
  runWithBoundaryProtection(vm, owner, noop, () => {
    // job
    if ("production" !== 'production') ;
    callHook(thisValue, fn, [event]);
  }, noop);
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const registeredComponentMap = new Map();
/**
 * INTERNAL: This function can only be invoked by compiled code. The compiler
 * will prevent this function from being imported by userland code.
 * @param Ctor
 * @param metadata
 */
function registerComponent(
// We typically expect a LightningElementConstructor, but technically you can call this with anything
Ctor, metadata) {
  if (isFunction$1(Ctor)) {
    // TODO [#3331]: add validation to check the value of metadata.sel is not an empty string.
    registeredComponentMap.set(Ctor, metadata);
  }
  // chaining this method as a way to wrap existing assignment of component constructor easily,
  // without too much transformation
  return Ctor;
}
function getComponentRegisteredTemplate(Ctor) {
  return registeredComponentMap.get(Ctor)?.tmpl;
}
function getComponentRegisteredName(Ctor) {
  return registeredComponentMap.get(Ctor)?.sel;
}
function getComponentAPIVersion(Ctor) {
  const metadata = registeredComponentMap.get(Ctor);
  const apiVersion = metadata?.apiVersion;
  if (isUndefined$1(apiVersion)) {
    // This should only occur in our integration tests; in practice every component
    // is registered, and so this code path should not get hit. But to be safe,
    // return the lowest possible version.
    return LOWEST_API_VERSION;
  }
  return apiVersion;
}
function supportsSyntheticElementInternals(Ctor) {
  return registeredComponentMap.get(Ctor)?.enableSyntheticElementInternals || false;
}
function isComponentFeatureEnabled(Ctor) {
  const flag = registeredComponentMap.get(Ctor)?.componentFeatureFlag;
  // Default to true if not provided
  return flag?.value !== false;
}
function getComponentMetadata(Ctor) {
  return registeredComponentMap.get(Ctor);
}
function getTemplateReactiveObserver(vm) {
  const reactiveObserver = createReactiveObserver(() => {
    const {
      isDirty
    } = vm;
    if (isFalse(isDirty)) {
      markComponentAsDirty(vm);
      scheduleRehydration(vm);
    }
  });
  return reactiveObserver;
}
function resetTemplateObserverAndUnsubscribe(vm) {
  const {
    tro,
    component
  } = vm;
  tro.reset();
  // Unsubscribe every time the template reactive observer is reset.
  if (lwcRuntimeFlags.ENABLE_EXPERIMENTAL_SIGNALS) {
    unsubscribeFromSignals(component);
  }
}
function renderComponent(vm) {
  // The engine should only hold a subscription to a signal if it is rendered in the template.
  // Because of the potential presence of conditional rendering logic, we unsubscribe on each render
  // in the scenario where it is present in one condition but not the other.
  // For example:
  // 1. There is an lwc:if=true conditional where the signal is present on the template.
  // 2. The lwc:if changes to false and the signal is no longer present on the template.
  // If the signal is still subscribed to, the template will re-render when it receives a notification
  // from the signal, even though we won't be using the new value.
  resetTemplateObserverAndUnsubscribe(vm);
  const vnodes = invokeComponentRenderMethod(vm);
  vm.isDirty = false;
  vm.isScheduled = false;
  return vnodes;
}
function markComponentAsDirty(vm) {
  vm.isDirty = true;
}
const cmpEventListenerMap = new WeakMap();
function getWrappedComponentsListener(vm, listener) {
  if (!isFunction$1(listener)) {
    throw new TypeError('Expected an EventListener but received ' + typeof listener); // avoiding problems with non-valid listeners
  }
  let wrappedListener = cmpEventListenerMap.get(listener);
  if (isUndefined$1(wrappedListener)) {
    wrappedListener = function (event) {
      invokeEventListener(vm, listener, undefined, event);
    };
    cmpEventListenerMap.set(listener, wrappedListener);
  }
  return wrappedListener;
}

/******************************************************************************
Copyright (c) Microsoft Corporation.

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
PERFORMANCE OF THIS SOFTWARE.
***************************************************************************** */
/* global Reflect, Promise, SuppressedError, Symbol, Iterator */

function __classPrivateFieldGet(receiver, state, kind, f) {
  if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot read private member from an object whose class did not declare it");
  return kind === "m" ? f : kind === "a" ? f.call(receiver) : f ? f.value : state.get(receiver);
}
function __classPrivateFieldSet(receiver, state, value, kind, f) {
  if (typeof state === "function" ? receiver !== state || true : !state.has(receiver)) throw new TypeError("Cannot write private member to an object whose class did not declare it");
  return state.set(receiver, value), value;
}
typeof SuppressedError === "function" ? SuppressedError : function (error, suppressed, message) {
  var e = new Error(message);
  return e.name = "SuppressedError", e.error = error, e.suppressed = suppressed, e;
};
var _ContextBinding_renderer, _ContextBinding_providedContextVarieties, _ContextBinding_elm;
class ContextBinding {
  constructor(vm, component, providedContextVarieties) {
    _ContextBinding_renderer.set(this, void 0);
    _ContextBinding_providedContextVarieties.set(this, void 0);
    _ContextBinding_elm.set(this, void 0);
    this.component = component;
    __classPrivateFieldSet(this, _ContextBinding_renderer, vm.renderer);
    __classPrivateFieldSet(this, _ContextBinding_elm, vm.elm);
    __classPrivateFieldSet(this, _ContextBinding_providedContextVarieties, providedContextVarieties);
    // Register the component as a context provider.
    __classPrivateFieldGet(this, _ContextBinding_renderer, "f").registerContextProvider(__classPrivateFieldGet(this, _ContextBinding_elm, "f"), ContextEventName, contextConsumer => {
      // This callback is invoked when the provided context is consumed somewhere down
      // in the component's subtree.
      return contextConsumer.setNewContext(__classPrivateFieldGet(this, _ContextBinding_providedContextVarieties, "f"));
    });
  }
  provideContext(contextVariety, providedContextSignal) {
    if (__classPrivateFieldGet(this, _ContextBinding_providedContextVarieties, "f").has(contextVariety)) {
      logWarnOnce('Multiple contexts of the same variety were provided. Only the first context will be used.');
      return;
    }
    __classPrivateFieldGet(this, _ContextBinding_providedContextVarieties, "f").set(contextVariety, providedContextSignal);
  }
  consumeContext(contextVariety, contextProvidedCallback) {
    __classPrivateFieldGet(this, _ContextBinding_renderer, "f").registerContextConsumer(__classPrivateFieldGet(this, _ContextBinding_elm, "f"), ContextEventName, {
      setNewContext: providerContextVarieties => {
        // If the provider has the specified context variety, then it is consumed
        // and true is returned to stop bubbling.
        if (providerContextVarieties.has(contextVariety)) {
          contextProvidedCallback(providerContextVarieties.get(contextVariety));
          return true;
        }
        // Return false as context has not been found/consumed
        // and the consumer should continue traversing the context tree
        return false;
      }
    });
  }
}
_ContextBinding_renderer = new WeakMap(), _ContextBinding_providedContextVarieties = new WeakMap(), _ContextBinding_elm = new WeakMap();
function connectContext(vm) {
  // Non-decorated objects
  connect(vm, keys(vm.cmpFields), vm.cmpFields);
  // Decorated objects like @api context
  connect(vm, keys(vm.cmpProps), vm.cmpProps);
}
function disconnectContext(vm) {
  // Non-decorated objects
  disconnect(vm, keys(vm.cmpFields), vm.cmpFields);
  // Decorated objects like @api context
  disconnect(vm, keys(vm.cmpProps), vm.cmpProps);
}
function connect(vm, enumerableKeys, contextContainer) {
  const contextKeys = getContextKeys();
  if (isUndefined$1(contextKeys)) {
    return;
  }
  const {
    connectContext
  } = contextKeys;
  const {
    component
  } = vm;
  const contextfulKeys = ArrayFilter.call(enumerableKeys, enumerableKey => isTrustedContext(contextContainer[enumerableKey]));
  if (contextfulKeys.length === 0) {
    return;
  }
  const providedContextVarieties = new Map();
  try {
    for (let i = 0; i < contextfulKeys.length; i++) {
      contextContainer[contextfulKeys[i]][connectContext](new ContextBinding(vm, component, providedContextVarieties));
    }
  } catch (err) {
    logWarnOnce(`Attempted to connect to trusted context but received the following error: ${err.message}`);
  }
}
function disconnect(vm, enumerableKeys, contextContainer) {
  {
    return;
  }
}

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
let idx = 0;
/** The internal slot used to associate different objects the engine manipulates with the VM */
const ViewModelReflection = new WeakMap();
function callHook(cmp, fn, args = []) {
  return fn.apply(cmp, args);
}
function setHook(cmp, prop, newValue) {
  cmp[prop] = newValue;
}
function getHook(cmp, prop) {
  return cmp[prop];
}
function rerenderVM(vm) {
  rehydrate(vm);
}
function connectRootElement(elm) {
  const vm = getAssociatedVM(elm);
  // Usually means moving the element from one place to another, which is observable via
  // life-cycle hooks.
  if (vm.state === 1 /* VMState.connected */) {
    disconnectRootElement(elm);
  }
  runConnectedCallback(vm);
  rehydrate(vm);
}
function disconnectRootElement(elm) {
  const vm = getAssociatedVM(elm);
  resetComponentStateWhenRemoved(vm);
}
function appendVM(vm) {
  rehydrate(vm);
}
// just in case the component comes back, with this we guarantee re-rendering it
// while preventing any attempt to rehydration until after reinsertion.
function resetComponentStateWhenRemoved(vm) {
  const {
    state
  } = vm;
  if (state !== 2 /* VMState.disconnected */) {
    // Making sure that any observing record will not trigger the rehydrated on this vm
    resetTemplateObserverAndUnsubscribe(vm);
    runDisconnectedCallback(vm);
    // Spec: https://dom.spec.whatwg.org/#concept-node-remove (step 14-15)
    runChildNodesDisconnectedCallback(vm);
    runLightChildNodesDisconnectedCallback(vm);
  }
}
// this method is triggered by the diffing algo only when a vnode from the
// old vnode.children is removed from the DOM.
function removeVM(vm) {
  resetComponentStateWhenRemoved(vm);
}
function getNearestShadowAncestor(owner) {
  let ancestor = owner;
  while (!isNull(ancestor) && ancestor.renderMode === 0 /* RenderMode.Light */) {
    ancestor = ancestor.owner;
  }
  return ancestor;
}
function createVM(elm, ctor, renderer, options) {
  const {
    mode,
    owner,
    tagName,
    hydrated
  } = options;
  const def = getComponentInternalDef(ctor);
  const apiVersion = getComponentAPIVersion(ctor);
  const vm = {
    elm,
    def,
    idx: idx++,
    state: 0 /* VMState.created */,
    isScheduled: false,
    isDirty: true,
    tagName,
    mode,
    owner,
    refVNodes: null,
    attachedEventListeners: new WeakMap(),
    children: EmptyArray,
    aChildren: EmptyArray,
    velements: EmptyArray,
    cmpProps: create(null),
    cmpFields: create(null),
    cmpSlots: {
      slotAssignments: create(null)
    },
    cmpTemplate: null,
    hydrated: Boolean(hydrated),
    renderMode: def.renderMode,
    context: {
      stylesheetToken: undefined,
      hasTokenInClass: undefined,
      hasTokenInAttribute: undefined,
      legacyStylesheetToken: undefined,
      hasLegacyTokenInClass: undefined,
      hasLegacyTokenInAttribute: undefined,
      hasScopedStyles: undefined,
      styleVNodes: null,
      tplCache: EmptyObject,
      wiredConnecting: EmptyArray,
      wiredDisconnecting: EmptyArray
    },
    // Properties set right after VM creation.
    tro: null,
    shadowMode: null,
    shadowMigrateMode: false,
    stylesheets: null,
    // Properties set by the LightningElement constructor.
    component: null,
    shadowRoot: null,
    renderRoot: null,
    callHook,
    setHook,
    getHook,
    renderer,
    apiVersion
  };
  vm.stylesheets = computeStylesheets(vm, def.ctor);
  const computedShadowMode = computeShadowMode(def, vm.owner, renderer, hydrated);
  if (lwcRuntimeFlags.ENABLE_FORCE_SHADOW_MIGRATE_MODE) {
    vm.shadowMode = 0 /* ShadowMode.Native */;
    vm.shadowMigrateMode = computedShadowMode === 1 /* ShadowMode.Synthetic */;
  } else {
    vm.shadowMode = computedShadowMode;
  }
  vm.tro = getTemplateReactiveObserver(vm);
  // Create component instance associated to the vm and the element.
  invokeComponentConstructor(vm, def.ctor);
  // Initializing the wire decorator per instance only when really needed
  if (hasWireAdapters(vm)) {
    installWireAdapters(vm);
  }
  return vm;
}
function validateComponentStylesheets(vm, stylesheets) {
  let valid = true;
  const validate = arrayOrStylesheet => {
    if (isArray$1(arrayOrStylesheet)) {
      for (let i = 0; i < arrayOrStylesheet.length; i++) {
        validate(arrayOrStylesheet[i]);
      }
    } else if (!isFunction$1(arrayOrStylesheet)) {
      // function assumed to be a stylesheet factory
      valid = false;
    }
  };
  if (!isArray$1(stylesheets)) {
    valid = false;
  } else {
    validate(stylesheets);
  }
  return valid;
}
// Validate and flatten any stylesheets defined as `static stylesheets`
function computeStylesheets(vm, ctor) {
  const {
    stylesheets
  } = ctor;
  if (!isUndefined$1(stylesheets)) {
    const valid = validateComponentStylesheets(vm, stylesheets);
    if (valid) {
      return flattenStylesheets(stylesheets);
    }
  }
  return null;
}
// Compute the shadowMode/renderMode without creating a VM. This is used in some scenarios like hydration.
function computeShadowAndRenderMode(Ctor, renderer) {
  const def = getComponentInternalDef(Ctor);
  const {
    renderMode
  } = def;
  // Assume null `owner` - this is what happens in hydration cases anyway
  // Also assume we are not in hydration mode for this exported API
  const shadowMode = computeShadowMode(def, /* owner */null, renderer, false);
  return {
    renderMode,
    shadowMode
  };
}
function computeShadowMode(def, owner, renderer, hydrated) {
  if (
  // Force the shadow mode to always be native. Used for running tests with synthetic shadow patches
  // on, but components running in actual native shadow mode
  // If synthetic shadow is explicitly disabled, use pure-native
  lwcRuntimeFlags.DISABLE_SYNTHETIC_SHADOW ||
  // hydration only supports native shadow
  isTrue(hydrated)) {
    return 0 /* ShadowMode.Native */;
  }
  const {
    isSyntheticShadowDefined
  } = renderer;
  let shadowMode;
  if (isSyntheticShadowDefined || lwcRuntimeFlags.ENABLE_FORCE_SHADOW_MIGRATE_MODE) {
    if (def.renderMode === 0 /* RenderMode.Light */) {
      // ShadowMode.Native implies "not synthetic shadow" which is consistent with how
      // everything defaults to native when the synthetic shadow polyfill is unavailable.
      shadowMode = 0 /* ShadowMode.Native */;
    } else if (def.shadowSupportMode === 'native') {
      shadowMode = 0 /* ShadowMode.Native */;
    } else {
      const shadowAncestor = getNearestShadowAncestor(owner);
      if (!isNull(shadowAncestor) && shadowAncestor.shadowMode === 0 /* ShadowMode.Native */) {
        // Transitive support for native Shadow DOM. A component in native mode
        // transitively opts all of its descendants into native.
        shadowMode = 0 /* ShadowMode.Native */;
      } else {
        // Synthetic if neither this component nor any of its ancestors are configured
        // to be native.
        shadowMode = 1 /* ShadowMode.Synthetic */;
      }
    }
  } else {
    // Native if the synthetic shadow polyfill is unavailable.
    shadowMode = 0 /* ShadowMode.Native */;
  }
  return shadowMode;
}
function associateVM(obj, vm) {
  ViewModelReflection.set(obj, vm);
}
function getAssociatedVM(obj) {
  const vm = ViewModelReflection.get(obj);
  return vm;
}
function getAssociatedVMIfPresent(obj) {
  const maybeVm = ViewModelReflection.get(obj);
  return maybeVm;
}
function rehydrate(vm) {
  if (isTrue(vm.isDirty)) {
    const children = renderComponent(vm);
    patchShadowRoot(vm, children);
  }
}
function patchShadowRoot(vm, newCh) {
  const {
    renderRoot,
    children: oldCh,
    renderer
  } = vm;
  // reset the refs; they will be set during `patchChildren`
  resetRefVNodes(vm);
  // caching the new children collection
  vm.children = newCh;
  if (newCh.length > 0 || oldCh.length > 0) {
    // patch function mutates vnodes by adding the element reference,
    // however, if patching fails it contains partial changes.
    if (oldCh !== newCh) {
      runWithBoundaryProtection(vm, vm, () => {
      }, () => {
        // job
        patchChildren(oldCh, newCh, renderRoot, renderer);
      }, () => {
      });
    }
  }
  if (vm.state === 1 /* VMState.connected */) {
    // If the element is connected, that means connectedCallback was already issued, and
    // any successive rendering should finish with the call to renderedCallback, otherwise
    // the connectedCallback will take care of calling it in the right order at the end of
    // the current rehydration process.
    runRenderedCallback(vm);
  }
}
function runRenderedCallback(vm) {
  const {
    def: {
      renderedCallback
    }
  } = vm;
  if (!isUndefined$1(renderedCallback)) {
    invokeComponentCallback(vm, renderedCallback);
  }
}
let rehydrateQueue = [];
function flushRehydrationQueue() {
  const vms = rehydrateQueue.sort((a, b) => a.idx - b.idx);
  rehydrateQueue = []; // reset to a new queue
  for (let i = 0, len = vms.length; i < len; i += 1) {
    const vm = vms[i];
    try {
      // We want to prevent rehydration from occurring when nodes are detached from the DOM as this can trigger
      // unintended side effects, like lifecycle methods being called multiple times.
      // For backwards compatibility, we use a flag to control the check.
      // 1. When flag is off, always rehydrate (legacy behavior)
      // 2. When flag is on, only rehydrate when the VM state is connected (fixed behavior)
      if (!lwcRuntimeFlags.DISABLE_DETACHED_REHYDRATION || vm.state === 1 /* VMState.connected */) {
        rehydrate(vm);
      }
    } catch (error) {
      if (i + 1 < len) {
        // pieces of the queue are still pending to be rehydrated, those should have priority
        if (rehydrateQueue.length === 0) {
          addCallbackToNextTick(flushRehydrationQueue);
        }
        ArrayUnshift.apply(rehydrateQueue, ArraySlice.call(vms, i + 1));
      }
      // re-throwing the original error will break the current tick, but since the next tick is
      // already scheduled, it should continue patching the rest.
      throw error;
    }
  }
}
function runConnectedCallback(vm) {
  const {
    state
  } = vm;
  if (state === 1 /* VMState.connected */) {
    return; // nothing to do since it was already connected
  }
  vm.state = 1 /* VMState.connected */;
  if (hasWireAdapters(vm)) {
    connectWireAdapters(vm);
  }
  if (lwcRuntimeFlags.ENABLE_EXPERIMENTAL_SIGNALS) {
    // Setup context before connected callback is executed
    connectContext(vm);
  }
  const {
    connectedCallback
  } = vm.def;
  if (!isUndefined$1(connectedCallback)) {
    invokeComponentCallback(vm, connectedCallback);
  }
  // This test only makes sense in the browser, with synthetic lifecycle, and when reporting is enabled or
  // we're in dev mode. This is to detect a particular issue with synthetic lifecycle.
  if (lwcRuntimeFlags.DISABLE_NATIVE_CUSTOM_ELEMENT_LIFECYCLE && (isReportingEnabled())) ;
}
function hasWireAdapters(vm) {
  return getOwnPropertyNames$1(vm.def.wire).length > 0;
}
function runDisconnectedCallback(vm) {
  if (lwcRuntimeFlags.ENABLE_EXPERIMENTAL_SIGNALS) {
    disconnectContext(vm);
  }
  if (isFalse(vm.isDirty)) {
    // this guarantees that if the component is reused/reinserted,
    // it will be re-rendered because we are disconnecting the reactivity
    // linking, so mutations are not automatically reflected on the state
    // of disconnected components.
    vm.isDirty = true;
  }
  vm.state = 2 /* VMState.disconnected */;
  if (hasWireAdapters(vm)) {
    disconnectWireAdapters(vm);
  }
  const {
    disconnectedCallback
  } = vm.def;
  if (!isUndefined$1(disconnectedCallback)) {
    invokeComponentCallback(vm, disconnectedCallback);
  }
}
function runChildNodesDisconnectedCallback(vm) {
  const {
    velements: vCustomElementCollection
  } = vm;
  // Reporting disconnection for every child in inverse order since they are
  // inserted in reserved order.
  for (let i = vCustomElementCollection.length - 1; i >= 0; i -= 1) {
    const {
      elm
    } = vCustomElementCollection[i];
    // There are two cases where the element could be undefined:
    // * when there is an error during the construction phase, and an error
    //   boundary picks it, there is a possibility that the VCustomElement
    //   is not properly initialized, and therefore is should be ignored.
    // * when slotted custom element is not used by the element where it is
    //   slotted into it, as  a result, the custom element was never
    //   initialized.
    if (!isUndefined$1(elm)) {
      const childVM = getAssociatedVMIfPresent(elm);
      // The VM associated with the element might be associated undefined
      // in the case where the VM failed in the middle of its creation,
      // eg: constructor throwing before invoking super().
      if (!isUndefined$1(childVM)) {
        resetComponentStateWhenRemoved(childVM);
      }
    }
  }
}
function runLightChildNodesDisconnectedCallback(vm) {
  const {
    aChildren: adoptedChildren
  } = vm;
  recursivelyDisconnectChildren(adoptedChildren);
}
/**
 * The recursion doesn't need to be a complete traversal of the vnode graph,
 * instead it can be partial, when a custom element vnode is found, we don't
 * need to continue into its children because by attempting to disconnect the
 * custom element itself will trigger the removal of anything slotted or anything
 * defined on its shadow.
 * @param vnodes
 */
function recursivelyDisconnectChildren(vnodes) {
  for (let i = 0, len = vnodes.length; i < len; i += 1) {
    const vnode = vnodes[i];
    if (!isNull(vnode) && !isUndefined$1(vnode.elm)) {
      switch (vnode.type) {
        case 2 /* VNodeType.Element */:
          recursivelyDisconnectChildren(vnode.children);
          break;
        case 3 /* VNodeType.CustomElement */:
          {
            const vm = getAssociatedVM(vnode.elm);
            resetComponentStateWhenRemoved(vm);
            break;
          }
      }
    }
  }
}
// This is a super optimized mechanism to remove the content of the root node (shadow root
// for shadow DOM components and the root element itself for light DOM) without having to go
// into snabbdom. Especially useful when the reset is a consequence of an error, in which case the
// children VNodes might not be representing the current state of the DOM.
function resetComponentRoot(vm) {
  recursivelyRemoveChildren(vm.children, vm);
  vm.children = EmptyArray;
  runChildNodesDisconnectedCallback(vm);
  vm.velements = EmptyArray;
}
// Helper function to remove all children of the root node.
// If the set of children includes VFragment nodes, we need to remove the children of those nodes too.
// Since VFragments can contain other VFragments, we need to traverse the entire of tree of VFragments.
// If the set contains no VFragment nodes, no traversal is needed.
function recursivelyRemoveChildren(vnodes, vm) {
  const {
    renderRoot,
    renderer: {
      remove
    }
  } = vm;
  for (let i = 0, len = vnodes.length; i < len; i += 1) {
    const vnode = vnodes[i];
    if (!isNull(vnode)) {
      // VFragments are special; their .elm property does not point to the root element since they have no single root.
      if (isVFragment(vnode)) {
        recursivelyRemoveChildren(vnode.children, vm);
      } else if (!isUndefined$1(vnode.elm)) {
        remove(vnode.elm, renderRoot);
      }
    }
  }
}
function scheduleRehydration(vm) {
  if (isTrue(vm.isScheduled)) {
    return;
  }
  vm.isScheduled = true;
  if (rehydrateQueue.length === 0) {
    addCallbackToNextTick(flushRehydrationQueue);
  }
  ArrayPush$1.call(rehydrateQueue, vm);
}
function getErrorBoundaryVM(vm) {
  let currentVm = vm;
  while (!isNull(currentVm)) {
    if (!isUndefined$1(currentVm.def.errorCallback)) {
      return currentVm;
    }
    currentVm = currentVm.owner;
  }
}
function runWithBoundaryProtection(vm, owner, pre, job, post) {
  let error;
  pre();
  try {
    job();
  } catch (e) {
    error = Object(e);
  } finally {
    post();
    if (!isUndefined$1(error)) {
      addErrorComponentStack(vm, error);
      const errorBoundaryVm = isNull(owner) ? undefined : getErrorBoundaryVM(owner);
      // Error boundaries are not in effect when server-side rendering. `errorCallback`
      // is intended to allow recovery from errors - changing the state of a component
      // and instigating a re-render. That is at odds with the single-pass, synchronous
      // nature of SSR. For that reason, all errors bubble up to the `renderComponent`
      // call site.
      if (isUndefined$1(errorBoundaryVm)) {
        throw error; // eslint-disable-line no-unsafe-finally
      }
      resetComponentRoot(vm); // remove offenders
      // error boundaries must have an ErrorCallback
      const errorCallback = errorBoundaryVm.def.errorCallback;
      invokeComponentCallback(errorBoundaryVm, errorCallback, [error, error.wcStack]);
    }
  }
}
function runFormAssociatedCustomElementCallback(vm, faceCb, args) {
  const {
    renderMode,
    shadowMode,
    def: {
      ctor
    }
  } = vm;
  if (shadowMode === 1 /* ShadowMode.Synthetic */ && renderMode !== 0 /* RenderMode.Light */ && !supportsSyntheticElementInternals(ctor)) {
    throw new Error('Form associated lifecycle methods are not available in synthetic shadow. Please use native shadow or light DOM.');
  }
  invokeComponentCallback(vm, faceCb, args);
}
function runFormAssociatedCallback(elm, form) {
  const vm = getAssociatedVM(elm);
  const {
    formAssociatedCallback
  } = vm.def;
  if (!isUndefined$1(formAssociatedCallback)) {
    runFormAssociatedCustomElementCallback(vm, formAssociatedCallback, [form]);
  }
}
function runFormDisabledCallback(elm, disabled) {
  const vm = getAssociatedVM(elm);
  const {
    formDisabledCallback
  } = vm.def;
  if (!isUndefined$1(formDisabledCallback)) {
    runFormAssociatedCustomElementCallback(vm, formDisabledCallback, [disabled]);
  }
}
function runFormResetCallback(elm) {
  const vm = getAssociatedVM(elm);
  const {
    formResetCallback
  } = vm.def;
  if (!isUndefined$1(formResetCallback)) {
    runFormAssociatedCustomElementCallback(vm, formResetCallback);
  }
}
function runFormStateRestoreCallback(elm, state, reason) {
  const vm = getAssociatedVM(elm);
  const {
    formStateRestoreCallback
  } = vm.def;
  if (!isUndefined$1(formStateRestoreCallback)) {
    runFormAssociatedCustomElementCallback(vm, formStateRestoreCallback, [state, reason]);
  }
}
function resetRefVNodes(vm) {
  const {
    cmpTemplate
  } = vm;
  vm.refVNodes = !isNull(cmpTemplate) && cmpTemplate.hasRefs ? create(null) : null;
}
// This is a "handoff" from synthetic-shadow to engine-core – we want to clean up after ourselves
// so nobody else can misuse these global APIs.
delete globalThis[KEY__NATIVE_GET_ELEMENT_BY_ID];
delete globalThis[KEY__NATIVE_QUERY_SELECTOR_ALL];
// Our detection logic relies on some modern browser features. We can just skip reporting the data
// for unsupported browsers
function supportsCssEscape() {
  return typeof CSS !== 'undefined' && isFunction$1(CSS.escape);
}
// If this page is not using synthetic shadow, then we don't need to install detection. Note
// that we are assuming synthetic shadow is loaded before LWC.
function isSyntheticShadowLoaded() {
  // We should probably be calling `renderer.isSyntheticShadowDefined`, but 1) we don't have access to the renderer,
  // and 2) this code needs to run in @lwc/engine-core, so it can access `logWarn()` and `report()`.
  return hasOwnProperty$1.call(Element.prototype, KEY__SHADOW_TOKEN);
}
// Detecting cross-root ARIA in synthetic shadow only makes sense for the browser
if (supportsCssEscape() && isSyntheticShadowLoaded()) ;
// Deeply freeze the entire array (of arrays) of stylesheet factory functions
function deepFreeze(stylesheets) {
  traverseStylesheets(stylesheets, subStylesheets => {
    freeze(subStylesheets);
  });
}
// Deep-traverse an array (of arrays) of stylesheet factory functions, and call the callback for every array/function
function traverseStylesheets(stylesheets, callback) {
  callback(stylesheets);
  for (let i = 0; i < stylesheets.length; i++) {
    const stylesheet = stylesheets[i];
    if (isArray$1(stylesheet)) {
      traverseStylesheets(stylesheet, callback);
    } else {
      callback(stylesheet);
    }
  }
}
function addLegacyStylesheetTokensShim(tmpl) {
  // When ENABLE_FROZEN_TEMPLATE is false, then we shim stylesheetTokens on top of stylesheetToken for anyone who
  // is accessing the old internal API (backwards compat). Details: W-14210169
  defineProperty(tmpl, 'stylesheetTokens', {
    enumerable: true,
    configurable: true,
    get() {
      const {
        stylesheetToken
      } = this;
      if (isUndefined$1(stylesheetToken)) {
        return stylesheetToken;
      }
      // Shim for the old `stylesheetTokens` property
      // See https://github.com/salesforce/lwc/pull/2332/files#diff-7901555acef29969adaa6583185b3e9bce475cdc6f23e799a54e0018cb18abaa
      return {
        hostAttribute: `${stylesheetToken}-host`,
        shadowAttribute: stylesheetToken
      };
    },
    set(value) {
      // If the value is null or some other exotic object, you would be broken anyway in the past
      // because the engine would try to access hostAttribute/shadowAttribute, which would throw an error.
      // However it may be undefined in newer versions of LWC, so we need to guard against that case.
      this.stylesheetToken = isUndefined$1(value) ? undefined : value.shadowAttribute;
    }
  });
}
function freezeTemplate(tmpl) {
  // TODO [#2782]: remove this flag and delete the legacy behavior
  if (lwcRuntimeFlags.ENABLE_FROZEN_TEMPLATE) {
    // Deep freeze the template
    freeze(tmpl);
    if (!isUndefined$1(tmpl.stylesheets)) {
      deepFreeze(tmpl.stylesheets);
    }
  } else {
    // template is not frozen - shim, report, and warn
    // this shim should be applied in both dev and prod
    addLegacyStylesheetTokensShim(tmpl);
  }
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
//
// Feature detection
//
// This check for constructable style sheets is similar to Fast's:
// https://github.com/microsoft/fast/blob/d49d1ec/packages/web-components/fast-element/src/dom.ts#L51-L53
// See also: https://github.com/whatwg/webidl/issues/1027#issuecomment-934510070
const supportsConstructableStylesheets = isFunction$1(CSSStyleSheet.prototype.replaceSync) && isArray$1(document.adoptedStyleSheets);
const stylesheetCache = new Map();
function createFreshStyleElement(content) {
  const elm = document.createElement('style');
  elm.type = 'text/css';
  elm.textContent = content;
  // Add an attribute to distinguish global styles added by LWC as opposed to other frameworks/libraries on the page
  elm.setAttribute('data-rendered-by-lwc', '');
  return elm;
}
function createStyleElement(content, cacheData) {
  const {
    element,
    usedElement
  } = cacheData;
  // If the <style> was already used, then we should clone it. We cannot insert
  // the same <style> in two places in the DOM.
  if (usedElement) {
    // This `<style>` may be repeated multiple times in the DOM, so cache it. It's a bit
    // faster to call `cloneNode()` on an existing node than to recreate it every time.
    return element.cloneNode(true);
  }
  // We don't clone every time, because that would be a perf tax on the first time
  cacheData.usedElement = true;
  return element;
}
function createConstructableStylesheet(content) {
  const stylesheet = new CSSStyleSheet();
  stylesheet.replaceSync(content);
  return stylesheet;
}
function insertConstructableStylesheet(content, target, cacheData, signal) {
  const {
    adoptedStyleSheets
  } = target;
  const {
    stylesheet
  } = cacheData;
  // The reason we prefer .push() rather than reassignment is for perf: https://github.com/salesforce/lwc/pull/2683
  adoptedStyleSheets.push(stylesheet);
}
function insertStyleElement(content, target, cacheData, signal) {
  const elm = createStyleElement(content, cacheData);
  target.appendChild(elm);
}
function getCacheData(content, useConstructableStylesheet) {
  let cacheData = stylesheetCache.get(content);
  if (isUndefined$1(cacheData)) {
    cacheData = {
      stylesheet: undefined,
      element: undefined,
      roots: undefined,
      global: false,
      usedElement: false
    };
    stylesheetCache.set(content, cacheData);
  }
  // Create <style> elements or CSSStyleSheets on-demand, as needed
  if (useConstructableStylesheet && isUndefined$1(cacheData.stylesheet)) {
    cacheData.stylesheet = createConstructableStylesheet(content);
  } else if (!useConstructableStylesheet && isUndefined$1(cacheData.element)) {
    cacheData.element = createFreshStyleElement(content);
  }
  return cacheData;
}
function insertGlobalStylesheet(content, signal) {
  // Force a <style> element for global stylesheets. See comment below.
  const cacheData = getCacheData(content, false);
  if (cacheData.global) {
    // already inserted
    return;
  }
  cacheData.global = true; // mark inserted
  // TODO [#2922]: use document.adoptedStyleSheets in supported browsers. Currently we can't, due to backwards compat.
  insertStyleElement(content, document.head, cacheData);
}
function insertLocalStylesheet(content, target, signal) {
  const cacheData = getCacheData(content, supportsConstructableStylesheets);
  let {
    roots
  } = cacheData;
  if (isUndefined$1(roots)) {
    roots = cacheData.roots = new WeakSet(); // lazily initialize (not needed for global styles)
  } else if (roots.has(target)) {
    // already inserted
    return;
  }
  roots.add(target); // mark inserted
  // Constructable stylesheets are only supported in certain browsers:
  // https://caniuse.com/mdn-api_document_adoptedstylesheets
  // The reason we use it is for perf: https://github.com/salesforce/lwc/pull/2460
  if (supportsConstructableStylesheets) {
    insertConstructableStylesheet(content, target, cacheData);
  } else {
    // Fall back to <style> element
    insertStyleElement(content, target, cacheData);
  }
}
/**
 * Injects a stylesheet into the global (document) level or inside a shadow root.
 * @param content CSS content to insert
 * @param target ShadowRoot to insert into, or undefined if global (document) level
 * @param signal AbortSignal for aborting the stylesheet render. Used in dev mode for HMR to unrender stylesheets.
 */
function insertStylesheet(content, target, signal) {
  if (isUndefined$1(target)) {
    // global
    insertGlobalStylesheet(content);
  } else {
    // local
    insertLocalStylesheet(content, target);
  }
}

/*
 * Copyright (c) 2023, Salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const cachedConstructors = new Map();
const nativeLifecycleElementsToUpgradedByLWC = new WeakMap();
let elementBeingUpgradedByLWC = false;
let BaseUpgradableConstructor;
let BaseHTMLElement;
function createBaseUpgradableConstructor() {
  // Creates a constructor that is intended to be used directly as a custom element, except that the upgradeCallback is
  // passed in to the constructor so LWC can reuse the same custom element constructor for multiple components.
  // Another benefit is that only LWC can create components that actually do anything – if you do
  // `customElements.define('x-foo')`, then you don't have access to the upgradeCallback, so it's a dummy custom element.
  // This class should be created once per tag name.
  // TODO [#2972]: this class should expose observedAttributes as necessary
  BaseUpgradableConstructor = class TheBaseUpgradableConstructor extends HTMLElement {
    constructor(upgradeCallback, useNativeLifecycle) {
      super();
      if (useNativeLifecycle) {
        // When in native lifecycle mode, we need to keep track of instances that were created outside LWC
        // (i.e. not created by `lwc.createElement()`). If the element uses synthetic lifecycle, then we don't
        // need to track this.
        nativeLifecycleElementsToUpgradedByLWC.set(this, elementBeingUpgradedByLWC);
      }
      // If the element is not created using lwc.createElement(), e.g. `document.createElement('x-foo')`,
      // then elementBeingUpgradedByLWC will be false
      if (elementBeingUpgradedByLWC) {
        upgradeCallback(this);
      }
      // TODO [#2970]: LWC elements cannot be upgraded via new Ctor()
      // Do we want to support this? Throw an error? Currently for backwards compat it's a no-op.
    }
    connectedCallback() {
      // native `connectedCallback`/`disconnectedCallback` are only enabled in native lifecycle mode
      if (isTrue(nativeLifecycleElementsToUpgradedByLWC.get(this))) {
        connectRootElement(this);
      }
    }
    disconnectedCallback() {
      // native `connectedCallback`/`disconnectedCallback` are only enabled in native lifecycle mode
      if (isTrue(nativeLifecycleElementsToUpgradedByLWC.get(this))) {
        disconnectRootElement(this);
      }
    }
    formAssociatedCallback(form) {
      runFormAssociatedCallback(this, form);
    }
    formDisabledCallback(disabled) {
      runFormDisabledCallback(this, disabled);
    }
    formResetCallback() {
      runFormResetCallback(this);
    }
    formStateRestoreCallback(state, reason) {
      runFormStateRestoreCallback(this, state, reason);
    }
    /*LWC compiler v9.2.2*/
  };
  BaseHTMLElement = HTMLElement; // cache to track if it changes
}
const createUpgradableConstructor = isFormAssociated => {
  if (HTMLElement !== BaseHTMLElement) {
    // If the global HTMLElement changes out from under our feet, then we need to create a new
    // BaseUpgradableConstructor from scratch (since it extends from HTMLElement). This can occur if
    // polyfills are in play, e.g. a polyfill for scoped custom element registries.
    // This workaround can potentially be removed when W-15361244 is resolved.
    createBaseUpgradableConstructor();
  }
  // Using a BaseUpgradableConstructor superclass here is a perf optimization to avoid
  // re-defining the same logic (connectedCallback, disconnectedCallback, etc.) over and over.
  class UpgradableConstructor extends BaseUpgradableConstructor {
    /*LWC compiler v9.2.2*/
  }
  if (isFormAssociated) {
    // Perf optimization - the vast majority of components have formAssociated=false,
    // so we can skip the setter in those cases, since undefined works the same as false.
    UpgradableConstructor.formAssociated = isFormAssociated;
  }
  return UpgradableConstructor;
};
function getUpgradableConstructor(tagName, isFormAssociated) {
  let UpgradableConstructor = cachedConstructors.get(tagName);
  if (isUndefined$1(UpgradableConstructor)) {
    if (!isUndefined$1(customElements.get(tagName))) {
      throw new Error(`Unexpected tag name "${tagName}". This name is a registered custom element, preventing LWC to upgrade the element.`);
    }
    UpgradableConstructor = createUpgradableConstructor(isFormAssociated);
    customElements.define(tagName, UpgradableConstructor);
    cachedConstructors.set(tagName, UpgradableConstructor);
  }
  return UpgradableConstructor;
}
const createCustomElement = (tagName, upgradeCallback, useNativeLifecycle, isFormAssociated) => {
  const UpgradableConstructor = getUpgradableConstructor(tagName, isFormAssociated);
  if (Boolean(UpgradableConstructor.formAssociated) !== isFormAssociated) {
    throw new Error(`<${tagName}> was already registered with formAssociated=${UpgradableConstructor.formAssociated}. It cannot be re-registered with formAssociated=${isFormAssociated}. Please rename your component to have a different name than <${tagName}>`);
  }
  elementBeingUpgradedByLWC = true;
  try {
    return new UpgradableConstructor(upgradeCallback, useNativeLifecycle);
  } finally {
    elementBeingUpgradedByLWC = false;
  }
};

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
/**
 * A factory function that produces a renderer.
 * Renderer encapsulates operations that are required to render an LWC component into the underlying
 * runtime environment. In the case of @lwc/enigne-dom, it is meant to be used in a DOM environment.
 * @param baseRenderer Either null or the base renderer imported from 'lwc'.
 * @returns The created renderer
 * @example
 * import { renderer, rendererFactory } from 'lwc';
 * const customRenderer = rendererFactory(renderer);
 */
function rendererFactory(baseRenderer) {
  // Type assertion because this is replaced by rollup with an object, not a string.
  // See `injectInlineRenderer` in /scripts/rollup/rollup.config.js
  const renderer = function (exports$1) {
    /**
     * Copyright (c) 2026 Salesforce, Inc.
     */
    /*
     * Copyright (c) 2018, salesforce.com, inc.
     * All rights reserved.
     * SPDX-License-Identifier: MIT
     * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
     */
    /**
     *
     * @param value
     * @param msg
     */
    function invariant(value, msg) {
      if (!value) {
        throw new Error(`Invariant Violation: ${msg}`);
      }
    }
    /**
     *
     * @param value
     * @param msg
     */
    function isTrue$1(value, msg) {
      if (!value) {
        throw new Error(`Assert Violation: ${msg}`);
      }
    }
    /**
     *
     * @param value
     * @param msg
     */
    function isFalse$1(value, msg) {
      if (value) {
        throw new Error(`Assert Violation: ${msg}`);
      }
    }
    /**
     *
     * @param msg
     */
    function fail(msg) {
      throw new Error(msg);
    }
    var assert = /*#__PURE__*/Object.freeze({
      __proto__: null,
      fail: fail,
      invariant: invariant,
      isFalse: isFalse$1,
      isTrue: isTrue$1
    });

    /*
     * Copyright (c) 2024, Salesforce, Inc.
     * All rights reserved.
     * SPDX-License-Identifier: MIT
     * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
     */
    const {
      /** Detached {@linkcode Object.getOwnPropertyDescriptors}; see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/getOwnPropertyDescriptors MDN Reference}. */
      getOwnPropertyDescriptors
    } = Object;
    /**
     * Determines whether the argument is `undefined`.
     * @param obj Value to test
     * @returns `true` if the value is `undefined`.
     */
    function isUndefined(obj) {
      return obj === undefined;
    }
    /**
     * Determines whether the argument is `null`.
     * @param obj Value to test
     * @returns `true` if the value is `null`.
     */
    function isNull(obj) {
      return obj === null;
    }
    /** version: 9.2.2 */

    /*
     * Copyright (c) 2024, Salesforce, Inc.
     * All rights reserved.
     * SPDX-License-Identifier: MIT
     * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
     */
    // Like @lwc/shared, but for DOM APIs
    const ElementDescriptors = getOwnPropertyDescriptors(Element.prototype);
    const ElementAttachShadow = ElementDescriptors.attachShadow.value;
    const ElementShadowRootGetter = ElementDescriptors.shadowRoot.get;

    /*
     * Copyright (c) 2023, salesforce.com, inc.
     * All rights reserved.
     * SPDX-License-Identifier: MIT
     * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
     */
    class WireContextSubscriptionEvent extends CustomEvent {
      constructor(adapterToken, {
        setNewContext,
        setDisconnectedCallback
      }) {
        super(adapterToken, {
          bubbles: true,
          composed: true
        });
        this.setNewContext = setNewContext;
        this.setDisconnectedCallback = setDisconnectedCallback;
      }
      /*LWC compiler v9.2.2*/
    }
    function registerContextConsumer(elm, adapterContextToken, subscriptionPayload) {
      dispatchEvent(elm, new WireContextSubscriptionEvent(adapterContextToken, subscriptionPayload));
    }
    function registerContextProvider(elm, adapterContextToken, onContextSubscription) {
      addEventListener(elm, adapterContextToken, evt => {
        const {
          setNewContext,
          setDisconnectedCallback
        } = evt;
        // If context subscription is successful, stop event propagation
        if (onContextSubscription({
          setNewContext,
          setDisconnectedCallback
        })) {
          evt.stopImmediatePropagation();
        }
      });
    }

    /*
     * Copyright (c) 2018, salesforce.com, inc.
     * All rights reserved.
     * SPDX-License-Identifier: MIT
     * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
     */
    function cloneNode(node, deep) {
      return node.cloneNode(deep);
    }
    function createElement(tagName, namespace) {
      return isUndefined(namespace) ? document.createElement(tagName) : document.createElementNS(namespace, tagName);
    }
    function createText(content) {
      return document.createTextNode(content);
    }
    function createComment(content) {
      return document.createComment(content);
    }
    // Parse the fragment HTML string into DOM
    function createFragment(html) {
      const template = document.createElement('template');
      template.innerHTML = html;
      return template.content.firstChild;
    }
    function insert(node, parent, anchor) {
      parent.insertBefore(node, anchor);
    }
    function remove(node, parent) {
      parent.removeChild(node);
    }
    function nextSibling(node) {
      return node.nextSibling;
    }
    function previousSibling(node) {
      return node.previousSibling;
    }
    function getParentNode(node) {
      return node.parentNode;
    }
    function attachShadow(element, options) {
      // `shadowRoot` will be non-null in two cases:
      //   1. upon initial load with an SSR-generated DOM, while in Shadow render mode
      //   2. when a webapp author places <c-app> in their static HTML and mounts their
      //      root component with customElement.define('c-app', Ctor)
      // see W-17441501
      const shadowRoot = ElementShadowRootGetter.call(element);
      if (!isNull(shadowRoot)) {
        return shadowRoot;
      }
      return ElementAttachShadow.call(element, options);
    }
    function setText(node, content) {
      node.nodeValue = content;
    }
    function getProperty(node, key) {
      return node[key];
    }
    function setProperty(node, key, value) {
      node[key] = value;
    }
    function getAttribute(element, name, namespace) {
      return isUndefined(namespace) ? element.getAttribute(name) : element.getAttributeNS(namespace, name);
    }
    function setAttribute(element, name, value, namespace) {
      return isUndefined(namespace) ? element.setAttribute(name, value) : element.setAttributeNS(namespace, name, value);
    }
    function removeAttribute(element, name, namespace) {
      if (isUndefined(namespace)) {
        element.removeAttribute(name);
      } else {
        element.removeAttributeNS(namespace, name);
      }
    }
    function addEventListener(target, type, callback, options) {
      target.addEventListener(type, callback, options);
    }
    function removeEventListener(target, type, callback, options) {
      target.removeEventListener(type, callback, options);
    }
    function dispatchEvent(target, event) {
      return target.dispatchEvent(event);
    }
    function getClassList(element) {
      return element.classList;
    }
    function setCSSStyleProperty(element, name, value, important) {
      // TODO [#0]: How to avoid this type casting? Shall we use a different type interface to
      // represent elements in the engine?
      element.style.setProperty(name, value, important ? 'important' : '');
    }
    function getBoundingClientRect(element) {
      return element.getBoundingClientRect();
    }
    function querySelector(element, selectors) {
      return element.querySelector(selectors);
    }
    function querySelectorAll(element, selectors) {
      return element.querySelectorAll(selectors);
    }
    function getElementsByTagName(element, tagNameOrWildCard) {
      return element.getElementsByTagName(tagNameOrWildCard);
    }
    function getElementsByClassName(element, names) {
      return element.getElementsByClassName(names);
    }
    function getChildren(element) {
      return element.children;
    }
    function getChildNodes(element) {
      return element.childNodes;
    }
    function getFirstChild(element) {
      return element.firstChild;
    }
    function getFirstElementChild(element) {
      return element.firstElementChild;
    }
    function getLastChild(element) {
      return element.lastChild;
    }
    function getLastElementChild(element) {
      return element.lastElementChild;
    }
    function isConnected(node) {
      return node.isConnected;
    }
    function assertInstanceOfHTMLElement(elm, msg) {
      assert.invariant(elm instanceof HTMLElement, msg);
    }
    function ownerDocument(element) {
      return element.ownerDocument;
    }
    function getTagName(elm) {
      return elm.tagName;
    }
    function getStyle(elm) {
      return elm.style;
    }
    function attachInternals(elm) {
      return attachInternalsFunc.call(elm);
    }
    // Use the attachInternals method from HTMLElement.prototype because access to it is removed
    // in HTMLBridgeElement, ie: elm.attachInternals is undefined.
    // Additionally, cache the attachInternals method to protect against 3rd party monkey-patching.
    const attachInternalsFunc = typeof ElementInternals !== 'undefined' ? HTMLElement.prototype.attachInternals : () => {
      throw new Error('attachInternals API is not supported in this browser environment.');
    };
    exports$1.addEventListener = addEventListener;
    exports$1.assertInstanceOfHTMLElement = assertInstanceOfHTMLElement;
    exports$1.attachInternals = attachInternals;
    exports$1.attachShadow = attachShadow;
    exports$1.cloneNode = cloneNode;
    exports$1.createComment = createComment;
    exports$1.createElement = createElement;
    exports$1.createFragment = createFragment;
    exports$1.createText = createText;
    exports$1.dispatchEvent = dispatchEvent;
    exports$1.getAttribute = getAttribute;
    exports$1.getBoundingClientRect = getBoundingClientRect;
    exports$1.getChildNodes = getChildNodes;
    exports$1.getChildren = getChildren;
    exports$1.getClassList = getClassList;
    exports$1.getElementsByClassName = getElementsByClassName;
    exports$1.getElementsByTagName = getElementsByTagName;
    exports$1.getFirstChild = getFirstChild;
    exports$1.getFirstElementChild = getFirstElementChild;
    exports$1.getLastChild = getLastChild;
    exports$1.getLastElementChild = getLastElementChild;
    exports$1.getParentNode = getParentNode;
    exports$1.getProperty = getProperty;
    exports$1.getStyle = getStyle;
    exports$1.getTagName = getTagName;
    exports$1.insert = insert;
    exports$1.isConnected = isConnected;
    exports$1.nextSibling = nextSibling;
    exports$1.ownerDocument = ownerDocument;
    exports$1.previousSibling = previousSibling;
    exports$1.querySelector = querySelector;
    exports$1.querySelectorAll = querySelectorAll;
    exports$1.registerContextConsumer = registerContextConsumer;
    exports$1.registerContextProvider = registerContextProvider;
    exports$1.remove = remove;
    exports$1.removeAttribute = removeAttribute;
    exports$1.removeEventListener = removeEventListener;
    exports$1.setAttribute = setAttribute;
    exports$1.setCSSStyleProperty = setCSSStyleProperty;
    exports$1.setProperty = setProperty;
    exports$1.setText = setText;
    return exports$1;
  }({});
  // Meant to inherit any properties passed via the base renderer as the argument to the factory.
  Object.setPrototypeOf(renderer, baseRenderer);
  return renderer;
}

/*
 * Copyright (c) 2023, Salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
// Host element mutation tracking is for SSR only
const startTrackingMutations = noop;
const stopTrackingMutations = noop;
/**
 * The base renderer that will be used by engine-core.
 * This will be used for DOM operations when lwc is running in a browser environment.
 */
const renderer = assign(
// The base renderer will invoke the factory with null and assign additional properties that are
// shared across renderers
rendererFactory(null),
// Properties that are either not required to be sandboxed or rely on a globally shared information
{
  // insertStyleSheet implementation shares a global cache of stylesheet data
  insertStylesheet,
  // relies on a shared global cache
  createCustomElement,
  defineCustomElement: getUpgradableConstructor,
  isSyntheticShadowDefined: hasOwnProperty$1.call(Element.prototype, KEY__SHADOW_TOKEN),
  startTrackingMutations,
  stopTrackingMutations
});
function clearNode(node) {
  const childNodes = renderer.getChildNodes(node);
  for (let i = childNodes.length - 1; i >= 0; i--) {
    renderer.remove(childNodes[i], node);
  }
}
/**
 * The real `buildCustomElementConstructor`. Should not be accessible to external users!
 * @internal
 * @param Ctor LWC constructor to build
 * @returns A Web Component class
 * @see {@linkcode deprecatedBuildCustomElementConstructor}
 */
function buildCustomElementConstructor(Ctor) {
  var _a;
  const HtmlPrototype = getComponentHtmlPrototype(Ctor);
  const {
    observedAttributes
  } = HtmlPrototype;
  const {
    attributeChangedCallback
  } = HtmlPrototype.prototype;
  return _a = class extends HTMLElement {
    constructor() {
      super();
      if (!isNull(this.shadowRoot)) {
        clearNode(this.shadowRoot);
      }
      // Compute renderMode/shadowMode in advance. This must be done before `createVM` because `createVM` may
      // mutate the element.
      const {
        shadowMode,
        renderMode
      } = computeShadowAndRenderMode(Ctor, renderer);
      // Native shadow components are allowed to have pre-existing `childNodes` before upgrade. This supports
      // use cases where a custom element has declaratively-defined slotted content, e.g.:
      // https://github.com/salesforce/lwc/issues/3639
      const isNativeShadow = renderMode === 1 /* RenderMode.Shadow */ && shadowMode === 0 /* ShadowMode.Native */;
      if (!isNativeShadow && this.childNodes.length > 0) {
        clearNode(this);
      }
      createVM(this, Ctor, renderer, {
        mode: 'open',
        owner: null,
        tagName: this.tagName
      });
    }
    connectedCallback() {
      connectRootElement(this);
    }
    disconnectedCallback() {
      disconnectRootElement(this);
    }
    attributeChangedCallback(name, oldValue, newValue) {
      if (this instanceof BaseBridgeElement) {
        // W-17420330
        attributeChangedCallback.call(this, name, oldValue, newValue);
      }
    }
    formAssociatedCallback(form) {
      runFormAssociatedCallback(this, form);
    }
    formDisabledCallback(disabled) {
      runFormDisabledCallback(this, disabled);
    }
    formResetCallback() {
      runFormResetCallback(this);
    }
    formStateRestoreCallback(state, reason) {
      runFormStateRestoreCallback(this, state, reason);
    }
    /*LWC compiler v9.2.2*/
  }, _a.observedAttributes = observedAttributes,
  // Note CustomElementConstructor is not upgraded by LWC and inherits directly from HTMLElement which means it calls the native
  // attachInternals API.
  _a.formAssociated = Boolean(Ctor.formAssociated), _a;
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
// TODO [#2472]: Remove this workaround when appropriate.
// eslint-disable-next-line @lwc/lwc-internal/no-global-node
const _Node$1 = Node;
const ConnectingSlot = new WeakMap();
const DisconnectingSlot = new WeakMap();
function callNodeSlot(node, slot) {
  const fn = slot.get(node);
  if (!isUndefined$1(fn)) {
    fn(node);
  }
  return node; // for convenience
}
let monkeyPatched = false;
function monkeyPatchDomAPIs() {
  if (monkeyPatched) {
    // don't double-patch
    return;
  }
  monkeyPatched = true;
  // Monkey patching Node methods to be able to detect the insertions and removal of root elements
  // created via createElement.
  const {
    appendChild,
    insertBefore,
    removeChild,
    replaceChild
  } = _Node$1.prototype;
  assign(_Node$1.prototype, {
    appendChild(newChild) {
      const appendedNode = appendChild.call(this, newChild);
      return callNodeSlot(appendedNode, ConnectingSlot);
    },
    insertBefore(newChild, referenceNode) {
      const insertedNode = insertBefore.call(this, newChild, referenceNode);
      return callNodeSlot(insertedNode, ConnectingSlot);
    },
    removeChild(oldChild) {
      const removedNode = removeChild.call(this, oldChild);
      return callNodeSlot(removedNode, DisconnectingSlot);
    },
    replaceChild(newChild, oldChild) {
      const replacedNode = replaceChild.call(this, newChild, oldChild);
      callNodeSlot(replacedNode, DisconnectingSlot);
      callNodeSlot(newChild, ConnectingSlot);
      return replacedNode;
    }
  });
}
/**
 * EXPERIMENTAL: This function is almost identical to document.createElement with the slightly
 * difference that in the options, you can pass the `is` property set to a Constructor instead of
 * just a string value. The intent is to allow the creation of an element controlled by LWC without
 * having to register the element as a custom element.
 *
 * NOTE: The returned type incorrectly includes _all_ properties defined on the component class,
 * even though the runtime object only uses those decorated with `@api`. This is due to a
 * limitation of TypeScript. To avoid inferring incorrect properties, provide an explicit generic
 * parameter, e.g. `createElement<typeof LightningElement>('x-foo', { is: FooCtor })`.
 * @param sel The tagname of the element to create
 * @param options Control the behavior of the created element
 * @param options.is The LWC component that the element should be
 * @param options.mode What kind of shadow root to use
 * @returns The created HTML element
 * @throws Throws when called with invalid parameters.
 * @example
 * const el = createElement('x-foo', { is: FooCtor });
 */
function createElement(sel, options) {
  if (!isObject(options) || isNull(options)) {
    throw new TypeError(`"createElement" function expects an object as second parameter but received "${toString(options)}".`);
  }
  const Ctor = options.is;
  if (!isFunction$1(Ctor)) {
    throw new TypeError(`"createElement" function expects an "is" option with a valid component constructor.`);
  }
  const {
    createCustomElement
  } = renderer;
  // tagName must be all lowercase, unfortunately, we have legacy code that is
  // passing `sel` as a camel-case, which makes them invalid custom elements name
  // the following line guarantees that this does not leaks beyond this point.
  const tagName = StringToLowerCase.call(sel);
  const useNativeCustomElementLifecycle = !lwcRuntimeFlags.DISABLE_NATIVE_CUSTOM_ELEMENT_LIFECYCLE;
  const isFormAssociated = shouldBeFormAssociated(Ctor);
  // the custom element from the registry is expecting an upgrade callback
  /*
   * Note: if the upgradable constructor does not expect, or throw when we new it
   * with a callback as the first argument, we could implement a more advanced
   * mechanism that only passes that argument if the constructor is known to be
   * an upgradable custom element.
   */
  const upgradeCallback = elm => {
    createVM(elm, Ctor, renderer, {
      tagName,
      mode: options.mode !== 'closed' ? 'open' : 'closed',
      owner: null
    });
    if (!useNativeCustomElementLifecycle) {
      // Monkey-patch on-demand, because `lwcRuntimeFlags.DISABLE_NATIVE_CUSTOM_ELEMENT_LIFECYCLE` may be set to
      // `true` lazily, after `@lwc/engine-dom` has finished initializing but before a component has rendered.
      monkeyPatchDomAPIs();
      ConnectingSlot.set(elm, connectRootElement);
      DisconnectingSlot.set(elm, disconnectRootElement);
    }
  };
  return createCustomElement(tagName, upgradeCallback, useNativeCustomElementLifecycle, isFormAssociated);
}

/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
const ComponentConstructorToCustomElementConstructorMap = new Map();
function getCustomElementConstructor(Ctor) {
  if (Ctor === LightningElement) {
    throw new TypeError(`Invalid Constructor. LightningElement base class can't be claimed as a custom element.`);
  }
  let ce = ComponentConstructorToCustomElementConstructorMap.get(Ctor);
  if (isUndefined$1(ce)) {
    ce = buildCustomElementConstructor(Ctor);
    ComponentConstructorToCustomElementConstructorMap.set(Ctor, ce);
  }
  return ce;
}
/**
 * This static getter builds a Web Component class from a LWC constructor so it can be registered
 * as a new element via customElements.define() at any given time.
 * @example
 * import Foo from 'ns/foo';
 * customElements.define('x-foo', Foo.CustomElementConstructor);
 * const elm = document.createElement('x-foo');
 */
defineProperty(LightningElement, 'CustomElementConstructor', {
  get() {
    return getCustomElementConstructor(this);
  }
});
freeze(LightningElement);
seal(LightningElement.prototype);

/*
 * Copyright (c) 2024, Salesforce, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/MIT
 */
// Like @lwc/shared, but for DOM APIs
const ElementDescriptors = getOwnPropertyDescriptors(Element.prototype);
ElementDescriptors.attachShadow.value;
ElementDescriptors.shadowRoot.get;
/** version: 9.2.2 */

function stylesheet$5(token, useActualHostSelector, useNativeDirPseudoclass) {
  var shadowSelector = token ? ("[" + token + "]") : "";
  return [".app-frame", shadowSelector, " {min-height: 100vh;max-width: 100vw;overflow-x: hidden;background: #f7f8fa;color: #181818;}.auth-screen", shadowSelector, " {display: grid;min-height: 100vh;place-items: center;padding: 1rem;}.auth-panel", shadowSelector, " {width: min(100%, 28rem);padding: 1.25rem;border: 1px solid #dddbda;border-radius: 0.75rem;background: #fff;box-shadow: 0 12px 36px rgba(24, 24, 24, 0.08);}.auth-brand", shadowSelector, ",.brand", shadowSelector, " {display: flex;align-items: center;gap: 0.75rem;}.auth-brand", shadowSelector, " {margin-bottom: 1.5rem;}.auth-brand", shadowSelector, " h1", shadowSelector, " {margin: 0;font-size: 1.25rem;}.auth-brand", shadowSelector, " p", shadowSelector, ",.side-footer", shadowSelector, " span", shadowSelector, ",.admin-panel", shadowSelector, " label", shadowSelector, ",.user-row", shadowSelector, " span", shadowSelector, " {color: #5c5c5c;font-size: 0.8125rem;}.brand-mark", shadowSelector, " {display: inline-grid;width: 2.25rem;height: 2.25rem;place-items: center;border-radius: 0.375rem;background: #0176d3;color: #fff;font-size: 0.6875rem;font-weight: 800;}.auth-form", shadowSelector, ",.user-form", shadowSelector, " {display: grid;gap: 0.75rem;}.auth-form", shadowSelector, " label", shadowSelector, ",.admin-panel", shadowSelector, " label", shadowSelector, " {display: grid;gap: 0.25rem;font-weight: 700;}.side-nav", shadowSelector, " {position: fixed;inset: 0 auto 0 0;display: grid;grid-template-rows: auto minmax(0, 1fr) auto;width: 15rem;padding: 1rem;border-right: 1px solid #dddbda;background: #fff;box-shadow: 1px 0 0 rgba(24, 24, 24, 0.02);}.brand", shadowSelector, " {min-height: 3rem;font-weight: 800;}.side-nav", shadowSelector, " nav", shadowSelector, " {display: grid;align-content: start;gap: 0.25rem;padding-top: 1rem;}.nav-item", shadowSelector, ",.org-row", shadowSelector, " {display: block;min-width: 0;padding: 0.625rem 0.75rem;border: 1px solid transparent;border-radius: 0.5rem;color: #3e3e3c;text-decoration: none;font-weight: 700;}.nav-item:hover", shadowSelector, ",.nav-item.active", shadowSelector, ",.org-row:hover", shadowSelector, ",.org-row.selected", shadowSelector, " {border-color: #d8e6fe;background: #eef4ff;color: #032d60;}.side-footer", shadowSelector, " {display: grid;gap: 0.5rem;}.footer-user", shadowSelector, " {color: #181818;font-weight: 700;}.footer-model", shadowSelector, " {display: block;max-width: 100%;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;}.workspace", shadowSelector, " {min-height: 100vh;margin-left: 15rem;padding: 1rem;}.workspace-header", shadowSelector, " {display: flex;align-items: flex-end;justify-content: space-between;max-width: 72rem;margin: 0 auto 0.75rem;padding: 0.25rem 0;}.workspace-header", shadowSelector, " h1", shadowSelector, " {margin: 0;font-size: 1.375rem;font-weight: 800;}.workspace-header", shadowSelector, " p", shadowSelector, " {margin: 0.25rem 0 0;color: #5c5c5c;font-size: 0.875rem;}.settings-shell", shadowSelector, " {display: grid;grid-template-columns: 15rem minmax(0, 1fr);min-height: calc(100vh - 2rem);max-width: 76rem;margin: 0 auto;border: 1px solid #dddbda;border-radius: 0.5rem;overflow: hidden;background: #fff;box-shadow: 0 1px 2px rgba(24, 24, 24, 0.04);}.settings-sidebar", shadowSelector, " {padding: 1rem 0.75rem;border-right: 1px solid #e5e5e5;background: #fbfbfb;}.settings-title", shadowSelector, " {padding: 0.375rem 0.625rem 0.875rem;color: #5c5c5c;font-size: 0.75rem;font-weight: 800;text-transform: uppercase;}.settings-nav-item", shadowSelector, ",.mcp-server-option", shadowSelector, " {display: grid;width: 100%;border: 0;background: transparent;color: #2e2e2e;text-align: left;cursor: pointer;}.settings-nav-item", shadowSelector, " {min-height: 2.25rem;align-items: center;padding: 0 0.625rem;border-radius: 0.375rem;font-weight: 700;}.settings-nav-item:hover", shadowSelector, ",.settings-nav-item.active", shadowSelector, " {background: #eef4ff;color: #032d60;}.settings-main", shadowSelector, " {min-width: 0;padding: 1.5rem 2rem;}.settings-header", shadowSelector, " {display: flex;align-items: flex-start;justify-content: space-between;gap: 1rem;max-width: 52rem;margin: 0 auto 1.25rem;}.settings-header", shadowSelector, " p", shadowSelector, " {margin: 0 0 0.25rem;color: #5c5c5c;font-size: 0.8125rem;font-weight: 700;}.settings-header", shadowSelector, " h1", shadowSelector, ",.settings-card", shadowSelector, " h1", shadowSelector, " {margin: 0;color: #181818;font-size: 1.25rem;font-weight: 800;}.settings-feedback", shadowSelector, " {display: grid;gap: 0.625rem;max-width: 52rem;margin: 0 auto 1rem;padding: 0.75rem 0.875rem;border: 1px solid #c9c9c9;border-radius: 0.375rem;background: #f7f7f7;color: #181818;font-size: 0.875rem;}.settings-feedback.passed", shadowSelector, " {border-color: #91db8b;background: #eef8f1;color: #194e31;}.settings-feedback.failed", shadowSelector, " {border-color: #feb8ab;background: #fff1ee;color: #8e030f;}.settings-feedback.neutral", shadowSelector, " {border-color: #d8dde6;background: #f3f3f3;color: #444;}.validation-list", shadowSelector, " {display: grid;gap: 0.5rem;}.validation-row", shadowSelector, " {display: grid;gap: 0.125rem;padding-top: 0.5rem;border-top: 1px solid rgba(0, 0, 0, 0.08);}.validation-row", shadowSelector, " span", shadowSelector, " {font-weight: 800;}.validation-row", shadowSelector, " small", shadowSelector, " {line-height: 1.4;}.validation-row", shadowSelector, " code", shadowSelector, " {overflow: hidden;color: inherit;font-size: 0.75rem;text-overflow: ellipsis;white-space: nowrap;}.settings-card", shadowSelector, " {display: grid;gap: 0.875rem;max-width: 40rem;margin: 0 auto;}.settings-card", shadowSelector, " label", shadowSelector, ",.mcp-editor", shadowSelector, " label", shadowSelector, ",.field-group", shadowSelector, " {display: grid;gap: 0.375rem;color: #2e2e2e;font-size: 0.8125rem;font-weight: 700;}.mcp-settings-layout", shadowSelector, " {display: grid;grid-template-columns: 15rem minmax(0, 1fr);gap: 2rem;max-width: 52rem;margin: 0 auto;}.mcp-server-list", shadowSelector, " {display: grid;align-content: start;gap: 0.375rem;}.mcp-server-option", shadowSelector, " {gap: 0.125rem;padding: 0.625rem 0.75rem;border-radius: 0.375rem;}.mcp-server-option", shadowSelector, " span", shadowSelector, " {font-weight: 800;}.mcp-server-option", shadowSelector, " small", shadowSelector, " {color: #706e6b;font-size: 0.75rem;}.mcp-server-option:hover", shadowSelector, ",.mcp-server-option.active", shadowSelector, " {background: #f3f6fb;}.mcp-editor", shadowSelector, " {display: grid;gap: 1rem;}.toggle-label", shadowSelector, " {grid-template-columns: auto minmax(0, 1fr);align-items: center;justify-content: start;}.toggle-label", shadowSelector, " input", shadowSelector, " {margin: 0;}.field-label", shadowSelector, " {color: #2e2e2e;}.transport-picker", shadowSelector, " {display: inline-grid;grid-template-columns: repeat(2, minmax(8rem, auto));width: fit-content;overflow: hidden;border: 1px solid #c9c9c9;border-radius: 0.375rem;}.transport-option", shadowSelector, " {min-height: 2rem;padding: 0 0.875rem;border: 0;border-right: 1px solid #c9c9c9;background: #fff;color: #3e3e3c;font-size: 0.75rem;font-weight: 800;cursor: pointer;}.transport-option:last-child", shadowSelector, " {border-right: 0;}.transport-option.active", shadowSelector, " {background: #181818;color: #fff;}.repeat-row", shadowSelector, " {display: grid;grid-template-columns: minmax(0, 1fr) 2rem;gap: 0.5rem;align-items: center;}.repeat-row.two-col", shadowSelector, " {grid-template-columns: minmax(8rem, 0.8fr) minmax(0, 1.2fr) 2rem;}.icon-button", shadowSelector, " {display: grid;width: 2rem;height: 2rem;place-items: center;border: 1px solid #c9c9c9;border-radius: 0.375rem;background: #fff;color: #3e3e3c;font-size: 1.125rem;line-height: 1;cursor: pointer;}.text-action", shadowSelector, " {justify-self: start;min-height: 2rem;padding: 0;border: 0;background: transparent;color: #0176d3;font-weight: 800;cursor: pointer;}.monospace-input", shadowSelector, " {font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, \"Liberation Mono\", monospace;font-size: 0.8125rem;}.inline-form", shadowSelector, " {display: grid;grid-template-columns: minmax(0, 1fr) auto;gap: 0.5rem;}.org-row", shadowSelector, " {width: 100%;background: transparent;text-align: left;}.user-row", shadowSelector, " {display: grid;gap: 0.125rem;padding: 0.625rem 0;border-top: 1px solid #f3f2f2;}.mcp-panel", shadowSelector, " {align-content: start;}.mcp-row", shadowSelector, " {display: grid;gap: 0.5rem;padding: 0.75rem 0;border-top: 1px solid #f3f2f2;}.mcp-row:first-of-type", shadowSelector, " {border-top: 0;padding-top: 0;}.mcp-toggle", shadowSelector, " {display: grid;grid-template-columns: auto minmax(0, 1fr);align-items: start;gap: 0.5rem;color: #181818;}.mcp-toggle", shadowSelector, " input", shadowSelector, " {margin-top: 0.125rem;}.mcp-toggle", shadowSelector, " span", shadowSelector, " {display: grid;gap: 0.125rem;}.mcp-toggle", shadowSelector, " small", shadowSelector, " {color: #5c5c5c;font-weight: 400;line-height: 1.35;}.mcp-row", shadowSelector, " .slds-input", shadowSelector, " {font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, \"Liberation Mono\", monospace;font-size: 0.8125rem;}.floating-alert", shadowSelector, ",.export-toast", shadowSelector, " {position: fixed;right: 1rem;bottom: 1rem;z-index: 9000;max-width: min(32rem, calc(100vw - 2rem));border-radius: 0.375rem;}.export-toast", shadowSelector, " {display: grid;grid-template-columns: minmax(0, 1fr) auto;gap: 0.75rem;align-items: center;min-width: min(22rem, calc(100vw - 2rem));background: #eef8f1;color: #2e844a;}.toast-dismiss", shadowSelector, " {display: grid;width: 1.75rem;height: 1.75rem;place-items: center;border: 1px solid rgba(46, 132, 74, 0.24);border-radius: 0.25rem;background: transparent;color: #194e31;font-size: 1.25rem;font-weight: 700;line-height: 1;cursor: pointer;}.toast-dismiss:hover", shadowSelector, ",.toast-dismiss:focus", shadowSelector, " {background: rgba(46, 132, 74, 0.1);}@media (max-width: 980px) {.side-nav", shadowSelector, " {position: static;width: auto;border-right: 0;border-bottom: 1px solid #dddbda;}.side-nav", shadowSelector, " nav", shadowSelector, " {display: flex;overflow-x: auto;padding-top: 0.75rem;}.workspace", shadowSelector, " {margin-left: 0;}.side-footer", shadowSelector, " {padding-top: 0.75rem;}.settings-shell", shadowSelector, ", .mcp-settings-layout", shadowSelector, " {grid-template-columns: 1fr;}.settings-sidebar", shadowSelector, " {border-right: 0;border-bottom: 1px solid #e5e5e5;}.settings-main", shadowSelector, " {padding: 1rem;}.repeat-row.two-col", shadowSelector, " {grid-template-columns: 1fr 1fr 2rem;}}@media (max-width: 640px) {.workspace", shadowSelector, " {padding: 0.5rem;}.workspace-header", shadowSelector, " {display: block;}.inline-form", shadowSelector, " {grid-template-columns: 1fr;}}"].join('');
  /*LWC compiler v9.2.2*/
}
var _implicitStylesheets$5 = [stylesheet$5];

function stylesheet$4(token, useActualHostSelector, useNativeDirPseudoclass) {
  var shadowSelector = token ? ("[" + token + "]") : "";
  return [".chat-shell", shadowSelector, " {display: grid;grid-template-rows: auto minmax(0, 1fr) auto auto;min-height: calc(100vh - 2rem);max-width: 1080px;margin: 0 auto;}.chat-topbar", shadowSelector, " {display: flex;align-items: flex-start;justify-content: space-between;gap: 1rem;padding: 1rem 0 0.75rem;}.chat-title", shadowSelector, " {min-width: 0;}.chat-topbar", shadowSelector, " h1", shadowSelector, " {margin: 0;color: #181818;font-size: 1.25rem;font-weight: 700;}.chat-topbar", shadowSelector, " p", shadowSelector, ",.bubble", shadowSelector, " span", shadowSelector, ",.review-strip", shadowSelector, " span", shadowSelector, " {color: #5c5c5c;font-size: 0.8125rem;}.status-stack", shadowSelector, " {display: flex;flex-wrap: wrap;justify-content: flex-end;gap: 0.375rem;}.chip", shadowSelector, " {display: inline-flex;align-items: center;min-height: 1.625rem;padding: 0 0.625rem;border: 1px solid #dddbda;border-radius: 999px;background: #fff;color: #3e3e3c;font-size: 0.75rem;font-weight: 700;}.chip.status.ready", shadowSelector, ",.chip.status.succeeded", shadowSelector, " {border-color: #91db8b;background: #eef8f1;color: #2e844a;}.chip.status.planning", shadowSelector, ",.chip.status.running", shadowSelector, ",.chip.status.review", shadowSelector, ",.chip.status.waiting-approval", shadowSelector, " {border-color: #ffba90;background: #fff7e0;color: #8a5a00;}.chip.status.error", shadowSelector, ",.chip.status.failed", shadowSelector, " {border-color: #ffb3b7;background: #fff1f2;color: #ba0517;}.chat-scroll", shadowSelector, " {display: grid;align-content: end;gap: 1rem;min-height: 0;padding: 1rem 4rem 1.5rem;overflow-y: auto;}.chat-scroll.empty", shadowSelector, " {align-content: center;padding-bottom: 5rem;}.chat-scroll.empty", shadowSelector, " .message", shadowSelector, " {justify-self: center;width: min(100%, 46rem);}.message", shadowSelector, " {display: grid;grid-template-columns: 2.25rem minmax(0, 1fr);gap: 0.75rem;align-items: flex-start;}.message.user", shadowSelector, " {grid-template-columns: minmax(0, 1fr) 2.25rem;}.message.user", shadowSelector, " .avatar", shadowSelector, " {grid-column: 2;grid-row: 1;}.message.user", shadowSelector, " .bubble", shadowSelector, " {grid-column: 1;justify-self: end;background: #f4f4f4;color: #181818;border-color: transparent;box-shadow: none;}.message.user", shadowSelector, " .bubble", shadowSelector, " span", shadowSelector, " {color: #5c5c5c;}.avatar", shadowSelector, " {display: grid;width: 2.25rem;height: 2.25rem;place-items: center;border-radius: 999px;background: #eaf5fe;color: #032d60;font-size: 0.6875rem;font-weight: 800;}.bubble", shadowSelector, " {max-width: 45rem;padding: 0.75rem 0.875rem;border: 0;border-radius: 0.75rem;background: #fff;box-shadow: none;}.bubble", shadowSelector, " p", shadowSelector, " {margin: 0;color: inherit;white-space: pre-wrap;}.bubble", shadowSelector, " span", shadowSelector, " {display: block;margin-top: 0.5rem;}.trace-panel", shadowSelector, " {margin-top: 0.75rem;border: 1px solid #e5e5e5;border-radius: 0.5rem;background: #fbfbfb;}.trace-panel", shadowSelector, " summary", shadowSelector, " {display: flex;align-items: center;justify-content: space-between;gap: 0.75rem;min-height: 2rem;padding: 0 0.625rem;color: #2e2e2e;cursor: pointer;list-style: none;}.trace-panel", shadowSelector, " summary", shadowSelector, "::-webkit-details-marker {display: none;}.trace-panel", shadowSelector, " summary", shadowSelector, " strong", shadowSelector, " {font-size: 0.75rem;font-weight: 800;}.trace-panel", shadowSelector, " summary", shadowSelector, " em", shadowSelector, " {color: #706e6b;font-size: 0.75rem;font-style: normal;font-weight: 700;}.trace-list", shadowSelector, " {display: grid;gap: 0.5rem;margin: 0;padding: 0.25rem 0.625rem 0.625rem 1.25rem;}.trace-step", shadowSelector, " {padding: 0.5rem 0 0;border-top: 1px solid #ecebea;}.trace-line", shadowSelector, " {display: flex;align-items: center;justify-content: space-between;gap: 0.75rem;}.trace-line", shadowSelector, " strong", shadowSelector, " {color: #181818;font-size: 0.8125rem;}.trace-line", shadowSelector, " span", shadowSelector, ",.trace-step", shadowSelector, " p", shadowSelector, ",.trace-step", shadowSelector, " dt", shadowSelector, ",.trace-step", shadowSelector, " dd", shadowSelector, " {color: #5c5c5c;font-size: 0.75rem;}.trace-step.failed", shadowSelector, " .trace-line", shadowSelector, " strong", shadowSelector, " {color: #ba0517;}.trace-step", shadowSelector, " p", shadowSelector, " {margin: 0.25rem 0 0;}.trace-step", shadowSelector, " dl", shadowSelector, " {display: grid;gap: 0.25rem;margin: 0.5rem 0 0;}.trace-step", shadowSelector, " dl", shadowSelector, " div", shadowSelector, " {display: grid;grid-template-columns: 7rem minmax(0, 1fr);gap: 0.5rem;}.trace-step", shadowSelector, " dt", shadowSelector, " {font-weight: 800;}.trace-step", shadowSelector, " dd", shadowSelector, " {min-width: 0;margin: 0;overflow-wrap: anywhere;font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, \"Liberation Mono\", monospace;}.review-strip", shadowSelector, " {display: flex;align-items: center;justify-content: space-between;gap: 1rem;margin-bottom: 0.75rem;padding: 0.875rem 1rem;border: 1px solid #dddbda;border-radius: 0.5rem;background: #fff;}.review-strip", shadowSelector, " strong", shadowSelector, ",.review-strip", shadowSelector, " span", shadowSelector, " {display: block;}.review-actions", shadowSelector, " {display: flex;flex-wrap: wrap;justify-content: flex-end;gap: 0.5rem;}.composer", shadowSelector, " {display: grid;gap: 0.625rem;width: min(100%, 54rem);margin: 0 auto;padding: 0.875rem;border: 1px solid #dddbda;border-radius: 1rem;background: #fff;box-shadow: 0 8px 24px rgba(24, 24, 24, 0.08);}.composer-input", shadowSelector, " {min-height: 4rem;max-height: 11rem;resize: vertical;border: 0;outline: 0;color: #181818;font: inherit;}.composer-toolbar", shadowSelector, " {display: flex;align-items: center;justify-content: space-between;gap: 0.75rem;}.composer-tools", shadowSelector, " {display: flex;min-width: 0;flex-wrap: wrap;align-items: center;gap: 0.375rem;}.tool-button", shadowSelector, ",.send-button", shadowSelector, " {display: grid;width: 2rem;height: 2rem;flex: 0 0 auto;place-items: center;border: 1px solid #d8dde6;border-radius: 999px;background: #fff;color: #181818;font-size: 1.125rem;font-weight: 800;line-height: 1;cursor: pointer;}.composer-chip", shadowSelector, " {display: inline-flex;align-items: center;max-width: min(22rem, 100%);min-height: 2rem;padding: 0 0.625rem;overflow: hidden;border: 1px solid #d8dde6;border-radius: 999px;color: #3e3e3c;font-size: 0.75rem;font-weight: 700;text-overflow: ellipsis;white-space: nowrap;}.mode-picker", shadowSelector, " {display: inline-flex;min-height: 2rem;overflow: hidden;border: 1px solid #d8dde6;border-radius: 999px;background: #fff;}.mode-button", shadowSelector, " {min-width: 4.25rem;padding: 0 0.75rem;border: 0;border-right: 1px solid #d8dde6;background: transparent;color: #3e3e3c;font-size: 0.75rem;font-weight: 800;cursor: pointer;}.mode-button:last-child", shadowSelector, " {border-right: 0;}.mode-button:hover", shadowSelector, " {background: #f3f3f3;}.mode-button.active", shadowSelector, " {background: #181818;color: #fff;}.send-button", shadowSelector, " {border-color: #181818;background: #181818;color: #fff;}.send-button:disabled", shadowSelector, " {border-color: #c9c9c9;background: #c9c9c9;cursor: not-allowed;}@media (max-width: 720px) {.chat-shell", shadowSelector, " {min-height: calc(100vh - 1rem);}.chat-topbar", shadowSelector, ", .review-strip", shadowSelector, " {display: grid;}.status-stack", shadowSelector, ", .review-actions", shadowSelector, " {justify-content: flex-start;}.chat-scroll", shadowSelector, " {padding-right: 0;padding-left: 0;}.composer-toolbar", shadowSelector, " {align-items: flex-end;}}"].join('');
  /*LWC compiler v9.2.2*/
}
var _implicitStylesheets$4 = [stylesheet$4];

const $fragment1$5 = parseFragment`<header class="chat-topbar${0}"${2}><div class="chat-title${0}"${2}><h1${3}>Data 360 Agent</h1><p${3}>${"t5"}</p></div><div class="status-stack${0}" aria-label="Status"${2}><span${"c7"}${2}>${"t8"}</span></div></header>`;
const $fragment2$5 = parseFragment`<div class="avatar${0}"${2}>${"t1"}</div>`;
const $fragment3$5 = parseFragment`<p${3}>${"t1"}</p>`;
const $fragment4$5 = parseFragment`<span${3}>${"t1"}</span>`;
const $fragment5$5 = parseFragment`<summary${3}><strong${3}>Trace</strong><em${3}>${"t4"}</em></summary>`;
const $fragment6$5 = parseFragment`<strong${3}>${"t1"}</strong>`;
const $fragment7$5 = parseFragment`<span${3}>${"t1"}</span>`;
const $fragment8$4 = parseFragment`<p${3}>${"t1"}</p>`;
const $fragment9$4 = parseFragment`<div${3}><dt${3}>${"t2"}</dt><dd${3}>${"t4"}</dd></div>`;
const $fragment10$4 = parseFragment`<aside class="review-strip${0}" aria-label="Plan review"${2}><div${3}><strong${3}>${"t3"}</strong><span${3}>${"t5"}</span></div><div class="review-actions${0}"${2}><button class="slds-button slds-button_neutral${0}"${"a7:disabled"}${2}>Export PlanSpec</button><button class="slds-button slds-button_neutral${0}"${"a9:disabled"}${2}>${"t10"}</button><button class="slds-button slds-button_brand${0}"${"a11:disabled"}${2}>${"t12"}</button></div></aside>`;
const $fragment11$4 = parseFragment`<textarea class="composer-input${0}" placeholder="Ask Data 360 to set up a goal..."${2}>${"t1"}</textarea>`;
const $fragment12$4 = parseFragment`<button type="button" class="tool-button${0}" title="Attach context"${2}>+</button>`;
const $fragment13$4 = parseFragment`<button type="button"${"c0"}${"a0:data-mode"}${"a0:title"}${2}>${"t1"}</button>`;
const $fragment14$3 = parseFragment`<span class="composer-chip${0}"${2}>${"t1"}</span>`;
const $fragment15$3 = parseFragment`<span class="composer-chip${0}"${2}>${"t1"}</span>`;
const $fragment16$2 = parseFragment`<button class="send-button${0}"${"a0:title"}${"a0:disabled"}${2}>↑</button>`;
const stc0$5 = {
  classMap: {
    "chat-shell": true
  },
  attrs: {
    "aria-label": "Data 360 chat"
  },
  key: 0
};
const stc1$5 = {
  classMap: {
    "bubble": true
  },
  key: 7
};
const stc2$5 = {
  classMap: {
    "trace-panel": true
  },
  key: 12
};
const stc3$5 = {
  classMap: {
    "trace-list": true
  },
  key: 15
};
const stc4$5 = {
  classMap: {
    "trace-line": true
  },
  key: 17
};
const stc5$4 = {
  key: 24
};
const stc6$4 = {
  classMap: {
    "composer": true
  },
  key: 29
};
const stc7$4 = {
  classMap: {
    "composer-toolbar": true
  },
  key: 32
};
const stc8$3 = {
  classMap: {
    "composer-tools": true
  },
  key: 33
};
const stc9$3 = {
  classMap: {
    "mode-picker": true
  },
  attrs: {
    "role": "group",
    "aria-label": "Agent mode"
  },
  key: 36
};
function tmpl$5($api, $cmp, $slotset, $ctx) {
  const {d: api_dynamic_text, ncls: api_normalize_class_name, sp: api_static_part, st: api_static_fragment, k: api_key, h: api_element, i: api_iterator, b: api_bind} = $api;
  const {_m0, _m1, _m2, _m3, _m4, _m5, _m6, _m7, _m8} = $ctx;
  return [api_element("section", stc0$5, [api_static_fragment($fragment1$5, 2, [api_static_part(5, null, api_dynamic_text($cmp.subtitle)), api_static_part(7, {
    className: api_normalize_class_name($cmp.plannerStatusClass)
  }, null), api_static_part(8, null, api_dynamic_text($cmp.plannerStatus))]), api_element("div", {
    className: api_normalize_class_name($cmp.chatScrollClass),
    key: 3
  }, api_iterator($cmp.messageRows, function (message) {
    return api_element("article", {
      className: api_normalize_class_name(message.className),
      key: api_key(4, message.key)
    }, [api_static_fragment($fragment2$5, 6, [api_static_part(1, null, api_dynamic_text(message.avatar))]), api_element("div", stc1$5, [api_static_fragment($fragment3$5, 9, [api_static_part(1, null, api_dynamic_text(message.text))]), message.meta ? api_static_fragment($fragment4$5, 11, [api_static_part(1, null, api_dynamic_text(message.meta))]) : null, message.hasTrace ? api_element("details", stc2$5, [api_static_fragment($fragment5$5, 14, [api_static_part(4, null, api_dynamic_text(message.traceSummary))]), api_element("ol", stc3$5, api_iterator(message.traceRows, function (trace) {
      return api_element("li", {
        className: api_normalize_class_name(trace.className),
        key: api_key(16, trace.key)
      }, [api_element("div", stc4$5, [api_static_fragment($fragment6$5, 19, [api_static_part(1, null, api_dynamic_text(trace.label))]), trace.duration ? api_static_fragment($fragment7$5, 21, [api_static_part(1, null, api_dynamic_text(trace.duration))]) : null]), trace.detail ? api_static_fragment($fragment8$4, 23, [api_static_part(1, null, api_dynamic_text(trace.detail))]) : null, trace.hasData ? api_element("dl", stc5$4, api_iterator(trace.dataRows, function (item) {
        return api_static_fragment($fragment9$4, api_key(26, item.key), [api_static_part(2, null, api_dynamic_text(item.name)), api_static_part(4, null, api_dynamic_text(item.value))]);
      })) : null]);
    }))]) : null])]);
  })), $cmp.hasDraft ? api_static_fragment($fragment10$4, 28, [api_static_part(3, null, api_dynamic_text($cmp.planTitle)), api_static_part(5, null, api_dynamic_text($cmp.planSummary)), api_static_part(7, {
    on: _m0 || ($ctx._m0 = {
      "click": api_bind($cmp.handleExportPlanSpec)
    }),
    attrs: {
      "disabled": $cmp.exportPlanDisabled ? "" : null
    }
  }, null), api_static_part(9, {
    on: _m1 || ($ctx._m1 = {
      "click": api_bind($cmp.handleApprovePlan)
    }),
    attrs: {
      "disabled": $cmp.approveDisabled ? "" : null
    }
  }, null), api_static_part(10, null, api_dynamic_text($cmp.approveLabel)), api_static_part(11, {
    on: _m2 || ($ctx._m2 = {
      "click": api_bind($cmp.handleStartPlan)
    }),
    attrs: {
      "disabled": $cmp.startDisabled ? "" : null
    }
  }, null), api_static_part(12, null, api_dynamic_text($cmp.startLabel))]) : null, api_element("footer", stc6$4, [api_static_fragment($fragment11$4, 31, [api_static_part(0, {
    on: _m4 || ($ctx._m4 = {
      "input": api_bind($cmp.handleInput),
      "keydown": api_bind($cmp.handleKeydown)
    })
  }, null), api_static_part(1, null, api_dynamic_text($cmp.draftMessage))]), api_element("div", stc7$4, [api_element("div", stc8$3, [api_static_fragment($fragment12$4, 35), api_element("div", stc9$3, api_iterator($cmp.modeRows, function (mode) {
    return api_static_fragment($fragment13$4, api_key(38, mode.value), [api_static_part(0, {
      on: _m6 || ($ctx._m6 = {
        "click": api_bind($cmp.handleModeChange)
      }),
      className: api_normalize_class_name(mode.className),
      attrs: {
        "data-mode": mode.value,
        "title": mode.title
      }
    }, null), api_static_part(1, null, api_dynamic_text(mode.label))]);
  })), api_static_fragment($fragment14$3, 40, [api_static_part(1, null, api_dynamic_text($cmp.modelLabel))]), api_static_fragment($fragment15$3, 42, [api_static_part(1, null, api_dynamic_text($cmp.userLabel))])]), api_static_fragment($fragment16$2, 44, [api_static_part(0, {
    on: _m8 || ($ctx._m8 = {
      "click": api_bind($cmp.handleSend)
    }),
    attrs: {
      "title": $cmp.sendLabel,
      "disabled": $cmp.sendDisabled ? "" : null
    }
  }, null)])])])])];
  /*LWC compiler v9.2.2*/
}
var _tmpl$5 = registerTemplate(tmpl$5);
tmpl$5.stylesheets = [];
tmpl$5.stylesheetToken = "lwc-57sgcru7qck";
tmpl$5.legacyStylesheetToken = "c-chatWorkspace_chatWorkspace";
if (_implicitStylesheets$4) {
  tmpl$5.stylesheets.push.apply(tmpl$5.stylesheets, _implicitStylesheets$4);
}
freezeTemplate(tmpl$5);

function dateTime(value) {
  if (!value) return "";
  return new Intl.DateTimeFormat("en-US", {
    hour: "numeric",
    minute: "2-digit"
  }).format(new Date(value));
}
function number(value) {
  return new Intl.NumberFormat("en-US", {
    maximumFractionDigits: 0
  }).format(value || 0);
}
function slug(value) {
  return String(value || "").toLowerCase().replaceAll(" ", "-").replaceAll("_", "-");
}
function toJson(value) {
  const empty = !value || typeof value === "object" && !Object.keys(value).length;
  return empty ? "No data" : JSON.stringify(value, null, 2);
}

class ChatWorkspace extends LightningElement {
  constructor(...args) {
    super(...args);
    this.messages = [];
    this.plannerStatus = "Ready";
    this.modelLabel = "Model not configured";
    this.userLabel = "Not signed in";
    this.currentDraft = void 0;
    this.approvedPlan = void 0;
    this.currentRun = void 0;
    this.busy = {};
    this.chatMode = "auto";
    this.draftMessage = "";
  }
  get subtitle() {
    return this.currentRun?.status ? `Run ${this.currentRun.status}` : "Plan, review, and execute governed Data 360 setup.";
  }
  get plannerStatusClass() {
    return `chip status ${slug(this.plannerStatus || "ready")}`;
  }
  get chatScrollClass() {
    const userMessages = (this.messages || []).filter(message => message.role === "user").length;
    return `chat-scroll ${userMessages === 0 && !this.currentDraft ? "empty" : ""}`;
  }
  get messageRows() {
    const rows = this.messages?.length ? this.messages : [{
      role: "assistant",
      text: "What should Data 360 set up?",
      meta: "Describe the customer goal and the systems involved."
    }];
    return rows.map((message, index) => {
      const traceRows = normalizeTrace(message.trace, index);
      return {
        ...message,
        key: message.id || `${message.role}-${index}`,
        avatar: message.role === "user" ? "You" : "D360",
        className: `message ${message.role === "user" ? "user" : "assistant"}`,
        hasTrace: traceRows.length > 0,
        traceRows,
        traceSummary: traceRows.length === 1 ? "1 step" : `${traceRows.length} steps`
      };
    });
  }
  get hasDraft() {
    return Boolean(this.currentDraft?.plan);
  }
  get planTitle() {
    return this.currentDraft?.plan?.goal || "PlanSpec drafted";
  }
  get planSummary() {
    const steps = this.currentDraft?.plan?.steps?.length || 0;
    const validation = this.currentDraft?.validation?.ok ? "validated" : "needs review";
    return `${steps} ${steps === 1 ? "step" : "steps"} • ${validation}`;
  }
  get sendDisabled() {
    return !this.draftMessage.trim() || this.busy.draftPlan || this.busy.chat;
  }
  get sendLabel() {
    if (this.busy.chat) return "Thinking...";
    return this.busy.draftPlan ? "Planning..." : "Send";
  }
  get startDisabled() {
    return !this.approvedPlan || this.busy.startPlan || this.busy.approvePlan;
  }
  get approveDisabled() {
    return !this.currentDraft?.validation?.ok || this.busy.approvePlan || Boolean(this.approvedPlan);
  }
  get startLabel() {
    return this.busy.startPlan ? "Running..." : "Run plan";
  }
  get approveLabel() {
    if (this.busy.approvePlan) return "Approving...";
    return this.approvedPlan ? "Approved" : "Approve";
  }
  get exportPlanDisabled() {
    return !this.currentDraft?.plan?.id || this.busy.exportPlanSpec;
  }
  get modeRows() {
    return [{
      value: "auto",
      label: "Auto",
      title: "Decide between exploration and planning from the request."
    }, {
      value: "plan",
      label: "Plan",
      title: "Draft a governed PlanSpec for approval."
    }, {
      value: "execute",
      label: "Execute",
      title: "Use connected tools for live read-only exploration."
    }].map(mode => ({
      ...mode,
      className: `mode-button ${this.chatMode === mode.value ? "active" : ""}`
    }));
  }
  handleInput(event) {
    this.draftMessage = event.target.value;
  }
  handleKeydown(event) {
    if (event.key !== "Enter" || event.shiftKey) {
      return;
    }
    event.preventDefault();
    this.handleSend();
  }
  handleSend() {
    const text = this.draftMessage.trim();
    if (!text) return;
    this.draftMessage = "";
    const input = this.template.querySelector(".composer-input");
    if (input) {
      input.value = "";
    }
    this.dispatchEvent(new CustomEvent("sendmessage", {
      detail: {
        text,
        mode: this.chatMode
      }
    }));
  }
  handleModeChange(event) {
    this.dispatchEvent(new CustomEvent("chatmodechange", {
      detail: {
        mode: event.currentTarget.dataset.mode
      }
    }));
  }
  handleStartPlan() {
    this.dispatchEvent(new CustomEvent("startplan"));
  }
  handleApprovePlan() {
    this.dispatchEvent(new CustomEvent("approveplan"));
  }
  handleExportPlanSpec() {
    this.dispatchEvent(new CustomEvent("exportplanspec"));
  }
  /*LWC compiler v9.2.2*/
}
registerDecorators(ChatWorkspace, {
  publicProps: {
    messages: {
      config: 0
    },
    plannerStatus: {
      config: 0
    },
    modelLabel: {
      config: 0
    },
    userLabel: {
      config: 0
    },
    currentDraft: {
      config: 0
    },
    approvedPlan: {
      config: 0
    },
    currentRun: {
      config: 0
    },
    busy: {
      config: 0
    },
    chatMode: {
      config: 0
    }
  },
  fields: ["draftMessage"]
});
const __lwc_component_class_internal$5 = registerComponent(ChatWorkspace, {
  tmpl: _tmpl$5,
  sel: "c-chat-workspace",
  apiVersion: 66
});
function normalizeTrace(trace, messageIndex) {
  return (trace || []).map((entry, index) => {
    const dataRows = Object.entries(entry.data || {}).map(([name, value]) => ({
      key: `${messageIndex}-${index}-${name}`,
      name,
      value: traceValue(value)
    }));
    return {
      key: `${messageIndex}-${index}`,
      className: `trace-step ${slug(entry.status || "completed")}`,
      label: entry.label || entry.stage || "Trace step",
      detail: entry.detail || "",
      stage: entry.stage || "",
      status: entry.status || "",
      duration: typeof entry.durationMs === "number" ? `${entry.durationMs} ms` : "",
      dataRows,
      hasData: dataRows.length > 0
    };
  });
}
function traceValue(value) {
  if (value === null || value === undefined) {
    return "";
  }
  if (typeof value === "object") {
    return JSON.stringify(value);
  }
  return String(value);
}

function stylesheet$3(token, useActualHostSelector, useNativeDirPseudoclass) {
  var shadowSelector = token ? ("[" + token + "]") : "";
  return ".template-workspace" + shadowSelector + " {display: grid;grid-template-columns: minmax(260px, 340px) minmax(0, 1fr);gap: 0.75rem;align-items: start;}.slds-card" + shadowSelector + " {border-color: #dddbda;}.scenario-list" + shadowSelector + " {display: grid;gap: 0.5rem;}.scenario-row" + shadowSelector + " {display: grid;grid-template-columns: minmax(0, 1fr) auto;gap: 0.375rem 0.75rem;padding: 0.75rem;border: 1px solid #dddbda;border-radius: 0.25rem;background: #fff;cursor: pointer;}.scenario-row:hover" + shadowSelector + ",.scenario-row.selected" + shadowSelector + " {border-color: #1b96ff;background: #eef4ff;}.scenario-row:focus-visible" + shadowSelector + " {outline: 2px solid #1b96ff;outline-offset: 2px;}.scenario-row" + shadowSelector + " strong" + shadowSelector + ",.template-head" + shadowSelector + " strong" + shadowSelector + " {display: block;color: #181818;}.scenario-row" + shadowSelector + " span" + shadowSelector + ",.template-head" + shadowSelector + " span" + shadowSelector + ",.template-card" + shadowSelector + " p" + shadowSelector + ",.cloud-list" + shadowSelector + ",.architecture-note" + shadowSelector + " {color: #5c5c5c;font-size: 0.8125rem;}.scenario-row" + shadowSelector + " p" + shadowSelector + ",.scenario-row" + shadowSelector + " small" + shadowSelector + " {grid-column: 1 / -1;}.scenario-row" + shadowSelector + " p" + shadowSelector + " {margin: 0.25rem 0 0;}.scenario-row" + shadowSelector + " small" + shadowSelector + " {color: #706e6b;}.scenario-badge" + shadowSelector + " {visibility: hidden;}.scenario-row.selected" + shadowSelector + " .scenario-badge" + shadowSelector + " {visibility: visible;}.status-pills" + shadowSelector + " {display: flex;flex-wrap: wrap;justify-content: flex-end;gap: 0.375rem;}.template-grid" + shadowSelector + " {display: grid;grid-template-columns: repeat(2, minmax(0, 1fr));gap: 0.75rem;}.template-card" + shadowSelector + " {display: grid;gap: 0.625rem;min-width: 0;padding: 0.875rem;border: 1px solid #dddbda;border-radius: 0.25rem;background: #fff;}.template-head" + shadowSelector + " {display: flex;justify-content: space-between;gap: 1rem;}.template-card" + shadowSelector + " p" + shadowSelector + " {margin: 0;}.cloud-list" + shadowSelector + " {padding-top: 0.5rem;border-top: 1px solid #f3f2f2;color: #032d60;}.architecture-note" + shadowSelector + " {padding: 0.5rem;border-left: 3px solid #0176d3;background: #f3f8ff;}.template-actions" + shadowSelector + " {display: flex;justify-content: flex-end;gap: 0.5rem;margin-top: 0.25rem;}.empty-state" + shadowSelector + " {display: grid;min-height: 10rem;place-items: center;border: 1px dashed #dddbda;border-radius: 0.25rem;color: #706e6b;background: #fafaf9;}@media (max-width: 980px) {.template-workspace" + shadowSelector + ", .template-grid" + shadowSelector + " {grid-template-columns: 1fr;}.status-pills" + shadowSelector + ", .template-actions" + shadowSelector + " {justify-content: flex-start;}}";
  /*LWC compiler v9.2.2*/
}
var _implicitStylesheets$3 = [stylesheet$3];

const $fragment1$4 = parseFragment`<div class="slds-card__header slds-grid${0}"${2}><header class="slds-media slds-media_center slds-has-flexi-truncate${0}"${2}><div class="slds-media__body${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Scenarios</h2><p class="slds-text-body_small slds-text-color_weak${0}"${2}>Customer contexts for planning</p></div></header><span class="slds-badge${0}"${2}>${"t8"}</span></div>`;
const $fragment2$4 = parseFragment`<div${3}><strong${3}>${"t2"}</strong><span${3}>${"t4"}</span></div>`;
const $fragment3$4 = parseFragment`<span${"c0"}${2}>Selected</span>`;
const $fragment4$4 = parseFragment`<p${3}>${"t1"}</p>`;
const $fragment5$4 = parseFragment`<small${3}>${"t1"}</small>`;
const $fragment6$4 = parseFragment`<div class="empty-state${0}"${2}>No scenarios available.</div>`;
const $fragment7$4 = parseFragment`<div class="slds-card__header slds-grid${0}"${2}><header class="slds-media slds-media_center slds-has-flexi-truncate${0}"${2}><div class="slds-media__body${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Templates</h2><p class="slds-text-body_small slds-text-color_weak${0}"${2}>Reusable governed activation patterns</p></div></header><div class="status-pills${0}" aria-label="Current plan state"${2}><span class="slds-badge${0}"${2}>${"t9"}</span><span class="slds-badge${0}"${2}>${"t11"}</span><span class="slds-badge${0}"${2}>${"t13"}</span></div></div>`;
const $fragment8$3 = parseFragment`<div class="template-head${0}"${2}><div${3}><strong${3}>${"t3"}</strong><span${3}>${"t5"}</span></div><span class="slds-badge${0}"${2}>${"t7"}</span></div>`;
const $fragment9$3 = parseFragment`<p${3}>${"t1"}</p>`;
const $fragment10$3 = parseFragment`<div class="cloud-list${0}"${2}>${"t1"}</div>`;
const $fragment11$3 = parseFragment`<div class="architecture-note${0}"${2}>${"t1"}</div>`;
const $fragment12$3 = parseFragment`<div class="template-actions${0}"${2}><button class="slds-button slds-button_neutral${0}"${"a1:data-template-id"}${"a1:disabled"}${2}>${"t2"}</button><button class="slds-button slds-button_brand${0}"${"a3:data-template-id"}${"a3:disabled"}${2}>${"t4"}</button></div>`;
const $fragment13$3 = parseFragment`<div class="empty-state${0}"${2}>No templates available.</div>`;
const stc0$4 = {
  classMap: {
    "template-workspace": true
  },
  key: 0
};
const stc1$4 = {
  classMap: {
    "slds-card": true,
    "scenario-section": true
  },
  key: 1
};
const stc2$4 = {
  classMap: {
    "slds-card__body": true,
    "slds-card__body_inner": true
  },
  key: 4
};
const stc3$4 = {
  classMap: {
    "scenario-list": true
  },
  attrs: {
    "role": "listbox",
    "aria-label": "Scenarios"
  },
  key: 5
};
const stc4$4 = {
  classMap: {
    "slds-card": true,
    "template-section": true
  },
  key: 17
};
const stc5$3 = {
  classMap: {
    "slds-card__body": true,
    "slds-card__body_inner": true
  },
  key: 20
};
const stc6$3 = {
  classMap: {
    "template-grid": true
  },
  key: 21
};
const stc7$3 = {
  "template-card": true
};
function tmpl$4($api, $cmp, $slotset, $ctx) {
  const {d: api_dynamic_text, sp: api_static_part, st: api_static_fragment, ncls: api_normalize_class_name, k: api_key, b: api_bind, h: api_element, i: api_iterator, f: api_flatten} = $api;
  const {_m0, _m1, _m2} = $ctx;
  return [api_element("div", stc0$4, [api_element("section", stc1$4, [api_static_fragment($fragment1$4, 3, [api_static_part(8, null, api_dynamic_text($cmp.selectedScenarioName))]), api_element("div", stc2$4, [$cmp.hasScenarios ? api_element("div", stc3$4, api_iterator($cmp.scenarioRows, function (scenario) {
    return api_element("article", {
      className: api_normalize_class_name(scenario.className),
      attrs: {
        "role": "option",
        "aria-selected": scenario.selected,
        "tabindex": "0",
        "data-scenario-id": scenario.id
      },
      key: api_key(6, scenario.key),
      on: _m0 || ($ctx._m0 = {
        "click": api_bind($cmp.handleScenarioClick),
        "keydown": api_bind($cmp.handleScenarioKeydown)
      })
    }, [api_static_fragment($fragment2$4, 8, [api_static_part(2, null, api_dynamic_text(scenario.label)), api_static_part(4, null, api_dynamic_text(scenario.detail))]), api_static_fragment($fragment3$4, 10, [api_static_part(0, {
      className: api_normalize_class_name(scenario.badgeClass)
    }, null)]), api_static_fragment($fragment4$4, 12, [api_static_part(1, null, api_dynamic_text(scenario.utterance))]), scenario.hasMetrics ? api_static_fragment($fragment5$4, 14, [api_static_part(1, null, api_dynamic_text(scenario.metricLabel))]) : null]);
  })) : null, !$cmp.hasScenarios ? api_static_fragment($fragment6$4, 16) : null])]), api_element("section", stc4$4, [api_static_fragment($fragment7$4, 19, [api_static_part(9, null, api_dynamic_text($cmp.draftStatusLabel)), api_static_part(11, null, api_dynamic_text($cmp.runStatusLabel)), api_static_part(13, null, api_dynamic_text($cmp.planStepLabel))]), api_element("div", stc5$3, [$cmp.hasTemplates ? api_element("div", stc6$3, api_iterator($cmp.templateRows, function (item) {
    return api_element("article", {
      classMap: stc7$3,
      key: api_key(22, item.key)
    }, api_flatten([api_static_fragment($fragment8$3, 24, [api_static_part(3, null, api_dynamic_text(item.title)), api_static_part(5, null, api_dynamic_text(item.subtitle)), api_static_part(7, null, api_dynamic_text(item.stepLabel))]), api_static_fragment($fragment9$3, 26, [api_static_part(1, null, api_dynamic_text(item.summary))]), item.clouds ? api_static_fragment($fragment10$3, 28, [api_static_part(1, null, api_dynamic_text(item.clouds))]) : null, api_iterator(item.notes, function (note) {
      return api_static_fragment($fragment11$3, api_key(30, note), [api_static_part(1, null, api_dynamic_text(note))]);
    }), api_static_fragment($fragment12$3, 32, [api_static_part(1, {
      on: _m1 || ($ctx._m1 = {
        "click": api_bind($cmp.handleUseTemplate)
      }),
      attrs: {
        "data-template-id": item.id,
        "disabled": item.disabled ? "" : null
      }
    }, null), api_static_part(2, null, api_dynamic_text(item.useLabel)), api_static_part(3, {
      on: _m2 || ($ctx._m2 = {
        "click": api_bind($cmp.handleInstantiateTemplate)
      }),
      attrs: {
        "data-template-id": item.id,
        "disabled": item.disabled ? "" : null
      }
    }, null), api_static_part(4, null, api_dynamic_text(item.instantiateLabel))])]));
  })) : null, !$cmp.hasTemplates ? api_static_fragment($fragment13$3, 34) : null])])])];
  /*LWC compiler v9.2.2*/
}
var _tmpl$4 = registerTemplate(tmpl$4);
tmpl$4.stylesheets = [];
tmpl$4.stylesheetToken = "lwc-6mevdn5dhdi";
tmpl$4.legacyStylesheetToken = "c-templateWorkspace_templateWorkspace";
if (_implicitStylesheets$3) {
  tmpl$4.stylesheets.push.apply(tmpl$4.stylesheets, _implicitStylesheets$3);
}
freezeTemplate(tmpl$4);

class TemplateWorkspace extends LightningElement {
  constructor(...args) {
    super(...args);
    this.scenarios = [];
    this.templates = [];
    this.selectedScenarioId = "";
    this.currentDraft = void 0;
    this.currentRun = void 0;
    this.busy = {};
  }
  get scenarioRows() {
    return (this.scenarios || []).map(scenario => {
      const selected = scenario.id === this.selectedScenarioId;
      const utterance = scenario.defaultUtterances?.[0] || scenario.sourceSummary || "No default goal supplied.";
      const metricLabel = (scenario.goalMetrics || []).slice(0, 3).join(" • ");
      return {
        ...scenario,
        selected,
        key: scenario.id,
        label: scenario.name || scenario.id,
        detail: scenario.industry || "General",
        utterance,
        metricLabel,
        hasMetrics: Boolean(metricLabel),
        className: `scenario-row ${selected ? "selected" : ""}`,
        badgeClass: `slds-badge scenario-badge ${selected ? "slds-theme_success" : ""}`
      };
    });
  }
  get hasScenarios() {
    return this.scenarioRows.length > 0;
  }
  get templateRows() {
    return (this.templates || []).map(item => {
      const clouds = (item.clouds || []).slice(0, 4).join(" • ");
      const notes = (item.architectureNotes || []).slice(0, 2);
      const steps = item.steps || [];
      return {
        ...item,
        key: item.id,
        title: item.title || item.name || item.id,
        subtitle: [item.industry, item.complexity].filter(Boolean).join(" • ") || "Solution template",
        summary: item.summary || item.outcome || "No summary supplied.",
        clouds,
        notes,
        stepCount: steps.length,
        stepLabel: `${steps.length} ${steps.length === 1 ? "step" : "steps"}`,
        disabled: this.templateActionDisabled,
        useLabel: this.busy.useTemplate ? "Using..." : "Use",
        instantiateLabel: this.busy.instantiateTemplate ? "Instantiating..." : "Instantiate"
      };
    });
  }
  get hasTemplates() {
    return this.templateRows.length > 0;
  }
  get templateActionDisabled() {
    return Boolean(this.busy.useTemplate || this.busy.instantiateTemplate);
  }
  get selectedScenario() {
    return (this.scenarios || []).find(scenario => scenario.id === this.selectedScenarioId);
  }
  get selectedScenarioName() {
    return this.selectedScenario?.name || this.selectedScenarioId || "No scenario selected";
  }
  get draftStatusLabel() {
    if (!this.currentDraft?.plan) return "No draft";
    return this.currentDraft.validation?.ok ? "Draft validated" : "Draft needs review";
  }
  get runStatusLabel() {
    return this.currentRun?.status || "No run";
  }
  get planStepLabel() {
    const count = this.currentDraft?.plan?.steps?.length || 0;
    return `${count} ${count === 1 ? "step" : "steps"}`;
  }
  handleScenarioClick(event) {
    const scenarioId = event.currentTarget.dataset.scenarioId;
    const scenario = (this.scenarios || []).find(item => item.id === scenarioId);
    this.dispatchEvent(new CustomEvent("scenariochange", {
      detail: {
        scenarioId,
        scenario
      }
    }));
  }
  handleScenarioKeydown(event) {
    if (!["Enter", " "].includes(event.key)) return;
    event.preventDefault();
    this.handleScenarioClick(event);
  }
  handleUseTemplate(event) {
    const templateId = event.currentTarget.dataset.templateId;
    const template = (this.templates || []).find(item => item.id === templateId);
    this.dispatchEvent(new CustomEvent("usetemplate", {
      detail: {
        templateId,
        template
      }
    }));
  }
  handleInstantiateTemplate(event) {
    const templateId = event.currentTarget.dataset.templateId;
    const template = (this.templates || []).find(item => item.id === templateId);
    this.dispatchEvent(new CustomEvent("instantiatetemplate", {
      detail: {
        templateId,
        template
      }
    }));
  }
  /*LWC compiler v9.2.2*/
}
registerDecorators(TemplateWorkspace, {
  publicProps: {
    scenarios: {
      config: 0
    },
    templates: {
      config: 0
    },
    selectedScenarioId: {
      config: 0
    },
    currentDraft: {
      config: 0
    },
    currentRun: {
      config: 0
    },
    busy: {
      config: 0
    }
  }
});
const __lwc_component_class_internal$4 = registerComponent(TemplateWorkspace, {
  tmpl: _tmpl$4,
  sel: "c-template-workspace",
  apiVersion: 66
});

function stylesheet$2(token, useActualHostSelector, useNativeDirPseudoclass) {
  var shadowSelector = token ? ("[" + token + "]") : "";
  return [".plan-layout", shadowSelector, " {display: grid;grid-template-columns: minmax(240px, 280px) minmax(0, 1fr) minmax(320px, 0.46fr);gap: 0.75rem;align-items: start;}.slds-card", shadowSelector, " {border-color: #dddbda;}.request-actions", shadowSelector, " {display: grid;grid-template-columns: repeat(2, minmax(0, 1fr));gap: 0.5rem;}.request-actions", shadowSelector, " .slds-button", shadowSelector, " {width: 100%;justify-content: center;white-space: nowrap;}.request-actions", shadowSelector, " .slds-button:first-child", shadowSelector, " {grid-column: 1 / -1;}.plan-head", shadowSelector, " {display: flex;justify-content: space-between;gap: 1rem;padding-bottom: 0.75rem;border-bottom: 1px solid #dddbda;}.plan-head", shadowSelector, " strong", shadowSelector, " {color: #181818;}.plan-head", shadowSelector, " p", shadowSelector, " {color: #5c5c5c;font-size: 0.8125rem;}.review-stats", shadowSelector, " {display: grid;grid-template-columns: repeat(4, minmax(0, 1fr));gap: 1px;margin: 0.75rem 0;border: 1px solid #dddbda;border-radius: 0.25rem;overflow: hidden;background: #dddbda;}.review-stats", shadowSelector, " div", shadowSelector, " {min-width: 0;padding: 0.625rem;background: #fff;}.review-stats", shadowSelector, " strong", shadowSelector, ",.review-stats", shadowSelector, " span", shadowSelector, " {display: block;}.review-stats", shadowSelector, " span", shadowSelector, " {color: #5c5c5c;font-size: 0.6875rem;text-transform: uppercase;}.governance-grid", shadowSelector, " {display: grid;grid-template-columns: repeat(4, minmax(0, 1fr));gap: 0.5rem;margin: 0.75rem 0;}.governance-card", shadowSelector, " {min-width: 0;padding: 0.625rem;border: 1px solid #dddbda;border-left-width: 0.25rem;border-radius: 0.25rem;background: #fff;}.governance-card.good", shadowSelector, " {border-left-color: #2e844a;}.governance-card.warning", shadowSelector, " {border-left-color: #dd9a1e;}.governance-card.error", shadowSelector, " {border-left-color: #ba0517;}.governance-card.neutral", shadowSelector, " {border-left-color: #747474;}.governance-card", shadowSelector, " span", shadowSelector, ",.governance-card", shadowSelector, " p", shadowSelector, " {color: #706e6b;font-size: 0.6875rem;}.governance-card", shadowSelector, " span", shadowSelector, " {display: block;margin-bottom: 0.125rem;text-transform: uppercase;}.governance-card", shadowSelector, " strong", shadowSelector, " {display: block;overflow-wrap: anywhere;color: #181818;font-size: 0.875rem;}.governance-card", shadowSelector, " p", shadowSelector, " {margin-top: 0.125rem;line-height: 1.35;}.validation-ok", shadowSelector, " {justify-content: flex-start;min-height: auto;padding: 0.5rem 0.75rem;border-radius: 0.25rem;background: #eef8f1;color: #2e844a;}.timeline", shadowSelector, " {display: grid;grid-template-columns: repeat(auto-fit, minmax(18px, 1fr));gap: 0.25rem;margin: 0.75rem 0;}.timeline-dot", shadowSelector, " {height: 0.375rem;border-radius: 999px;background: #c9c9c9;}.timeline-dot.succeeded", shadowSelector, " {background: #2e844a;}.timeline-dot.running", shadowSelector, ",.timeline-dot.waiting-approval", shadowSelector, " {background: #dd9a1e;}.timeline-dot.failed", shadowSelector, ",.timeline-dot.error", shadowSelector, " {background: #ba0517;}.table-wrap", shadowSelector, " {overflow-x: auto;}.plan-view-tabs", shadowSelector, " {display: inline-grid;grid-auto-flow: column;gap: 0.125rem;margin: 0.75rem 0;padding: 0.125rem;border: 1px solid #dddbda;border-radius: 0.25rem;background: #f3f2f2;}.view-tab", shadowSelector, " {min-width: 5rem;border: 0;border-radius: 0.1875rem;padding: 0.375rem 0.75rem;background: transparent;color: #3e3e3c;font-weight: 600;}.view-tab.active", shadowSelector, " {background: #fff;color: #032d60;box-shadow: 0 1px 2px rgb(0 0 0 / 12%);}.plan-table", shadowSelector, " th", shadowSelector, ",.plan-table", shadowSelector, " td", shadowSelector, " {vertical-align: middle;}.plan-row", shadowSelector, " {cursor: pointer;}.plan-row.selected", shadowSelector, ",.plan-row:hover", shadowSelector, " {background: #eef4ff;}.plan-row:focus-visible", shadowSelector, " {outline: 2px solid #1b96ff;outline-offset: -2px;}.plan-row", shadowSelector, " th", shadowSelector, " span", shadowSelector, " {color: #706e6b;font-size: 0.75rem;font-weight: 400;}.status.succeeded", shadowSelector, " {background: #eef8f1;color: #2e844a;}.status.running", shadowSelector, ",.status.waiting-approval", shadowSelector, " {background: #fff7e0;color: #8a5a00;}.status.failed", shadowSelector, ",.status.error", shadowSelector, " {background: #fff1f2;color: #ba0517;}.dag-panel", shadowSelector, " {display: grid;gap: 0.75rem;}.section-header", shadowSelector, " {display: flex;justify-content: space-between;gap: 0.75rem;align-items: baseline;}.section-header", shadowSelector, " h3", shadowSelector, " {margin: 0;color: #181818;font-size: 0.8125rem;font-weight: 700;}.section-header", shadowSelector, " span", shadowSelector, " {color: #706e6b;font-size: 0.6875rem;text-transform: uppercase;}.dag-groups", shadowSelector, " {display: grid;grid-template-columns: repeat(3, minmax(12rem, 1fr));gap: 0.75rem;overflow-x: auto;}.dag-group", shadowSelector, " {min-width: 12rem;padding: 0.75rem;border: 1px solid #dddbda;border-radius: 0.25rem;background: #fafaf9;}.dag-group", shadowSelector, " h3", shadowSelector, " {margin-bottom: 0.625rem;color: #706e6b;font-size: 0.75rem;font-weight: 700;text-transform: uppercase;}.dag-node", shadowSelector, " {display: grid;width: 100%;gap: 0.375rem;margin-bottom: 0.5rem;padding: 0.625rem;border: 1px solid #c9c9c9;border-left-width: 0.25rem;border-radius: 0.25rem;background: #fff;color: #181818;text-align: left;}.dag-node:hover", shadowSelector, ",.dag-node.selected", shadowSelector, " {border-color: #1b96ff;box-shadow: 0 0 0 1px #1b96ff inset;}.dag-node.write", shadowSelector, ",.dag-node.publish", shadowSelector, ",.dag-node.activate", shadowSelector, ",.dag-node.destructive", shadowSelector, " {border-left-color: #dd9a1e;}.dag-node.read", shadowSelector, " {border-left-color: #0176d3;}.dag-node.succeeded", shadowSelector, " {background: #f3fcf5;}.dag-node.failed", shadowSelector, " {background: #fff1f2;}.dag-node-title", shadowSelector, " {font-weight: 700;}.dag-node-meta", shadowSelector, ",.dag-node-footer", shadowSelector, ",.dag-warning", shadowSelector, " {color: #706e6b;font-size: 0.6875rem;}.dag-node-meta", shadowSelector, " {overflow-wrap: anywhere;}.dag-node-footer", shadowSelector, " {display: flex;flex-wrap: wrap;gap: 0.375rem;}.dag-node-footer", shadowSelector, " span", shadowSelector, " {padding: 0.125rem 0.375rem;border-radius: 999px;background: #f3f2f2;}.dag-warning", shadowSelector, " {color: #8a5a00;}.dag-edge-list", shadowSelector, " {display: grid;gap: 0.375rem;max-height: 12rem;overflow: auto;padding: 0.5rem;border: 1px solid #dddbda;border-radius: 0.25rem;}.dag-edge", shadowSelector, " {display: grid;grid-template-columns: minmax(0, 1fr) auto minmax(0, 1fr);gap: 0.5rem;align-items: center;color: #3e3e3c;font-size: 0.75rem;}.dag-edge", shadowSelector, " strong", shadowSelector, " {overflow-wrap: anywhere;}.dag-edge", shadowSelector, " span", shadowSelector, " {border-radius: 999px;padding: 0.125rem 0.375rem;background: #eef4ff;color: #032d60;}.dag-edge.data", shadowSelector, " span", shadowSelector, " {background: #eef8f1;color: #2e844a;}.dag-edge", shadowSelector, " small", shadowSelector, " {grid-column: 1 / -1;color: #706e6b;}.visibility-grid", shadowSelector, " {display: grid;grid-template-columns: minmax(0, 1.25fr) minmax(16rem, 0.75fr);gap: 0.75rem;margin-top: 1rem;}.visibility-panel", shadowSelector, " {display: grid;align-content: start;gap: 0.75rem;min-width: 0;padding: 0.75rem;border: 1px solid #dddbda;border-radius: 0.25rem;background: #fff;}.signal-stack", shadowSelector, " {display: grid;gap: 0.375rem;}.signal-row", shadowSelector, " {display: grid;grid-template-columns: minmax(0, 1fr) auto;gap: 0.25rem 0.5rem;padding: 0.5rem;border: 1px solid #dddbda;border-left-width: 0.25rem;border-radius: 0.25rem;background: #fafaf9;}.signal-row.good", shadowSelector, " {border-left-color: #2e844a;}.signal-row.warning", shadowSelector, " {border-left-color: #dd9a1e;}.signal-row.error", shadowSelector, " {border-left-color: #ba0517;}.signal-row", shadowSelector, " strong", shadowSelector, " {overflow-wrap: anywhere;}.signal-row", shadowSelector, " span", shadowSelector, ",.signal-row", shadowSelector, " p", shadowSelector, " {color: #706e6b;font-size: 0.75rem;}.signal-row", shadowSelector, " p", shadowSelector, " {grid-column: 1 / -1;margin: 0;overflow-wrap: anywhere;}.binding-list", shadowSelector, " {display: grid;gap: 0.5rem;max-height: 20rem;overflow: auto;}.binding-row", shadowSelector, " {display: grid;gap: 0.5rem;padding: 0.5rem;border: 1px solid #dddbda;border-left-width: 0.25rem;border-radius: 0.25rem;background: #fafaf9;}.binding-row.frozen", shadowSelector, " {border-left-color: #2e844a;}.binding-row.catalog", shadowSelector, " {border-left-color: #747474;}.binding-row", shadowSelector, " strong", shadowSelector, ",.binding-row", shadowSelector, " span", shadowSelector, ",.binding-row", shadowSelector, " dd", shadowSelector, " {overflow-wrap: anywhere;}.binding-row", shadowSelector, " > div:first-child", shadowSelector, " span", shadowSelector, ",.binding-row", shadowSelector, " > span", shadowSelector, " {display: block;color: #706e6b;font-size: 0.75rem;}.binding-row", shadowSelector, " dl", shadowSelector, " {display: grid;grid-template-columns: repeat(5, minmax(0, 1fr));gap: 0.375rem;margin: 0;}.binding-row", shadowSelector, " dt", shadowSelector, " {color: #706e6b;font-size: 0.6875rem;text-transform: uppercase;}.binding-row", shadowSelector, " dd", shadowSelector, " {margin: 0;color: #032d60;font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;font-size: 0.6875rem;}.approval-history", shadowSelector, " {margin-top: 1rem;padding-top: 0.75rem;border-top: 1px solid #dddbda;}.approval-history", shadowSelector, " h3", shadowSelector, " {margin-bottom: 0.5rem;font-size: 0.8125rem;font-weight: 700;}.history-row", shadowSelector, " {display: grid;grid-template-columns: minmax(0, 1fr) auto;gap: 0.5rem;padding: 0.5rem 0;border-bottom: 1px solid #f3f2f2;}.history-row", shadowSelector, " p", shadowSelector, " {grid-column: 1 / -1;color: #706e6b;font-size: 0.75rem;}.history-row.plan-approval", shadowSelector, " {border-left: 0.25rem solid #2e844a;padding-left: 0.5rem;background: #f3fcf5;}.inspector-head", shadowSelector, " {display: flex;justify-content: space-between;gap: 1rem;align-items: flex-start;}.inspector-list", shadowSelector, " {display: grid;gap: 0.5rem;margin-top: 0.75rem;}.inspector-list", shadowSelector, " div", shadowSelector, " {display: grid;grid-template-columns: 5.25rem minmax(0, 1fr);gap: 0.5rem;}.inspector-list", shadowSelector, " dt", shadowSelector, " {color: #706e6b;font-size: 0.75rem;}.json-grid", shadowSelector, " {display: grid;grid-template-columns: repeat(2, minmax(0, 1fr));gap: 0.5rem;margin-top: 0.75rem;}.json-preview", shadowSelector, " {min-width: 0;padding: 0.5rem;border: 1px solid #dddbda;border-radius: 0.25rem;background: #fafaf9;}.json-preview", shadowSelector, " strong", shadowSelector, " {font-size: 0.75rem;}.json-preview", shadowSelector, " pre", shadowSelector, " {max-height: 14rem;margin: 0.375rem 0 0;overflow: auto;color: #032d60;font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;font-size: 0.6875rem;line-height: 1.4;white-space: pre-wrap;}.empty-state", shadowSelector, " {display: grid;min-height: 12rem;place-items: center;border: 1px dashed #dddbda;border-radius: 0.25rem;color: #706e6b;background: #fafaf9;}@media (max-width: 1450px) {.plan-layout", shadowSelector, " {grid-template-columns: minmax(240px, 280px) minmax(0, 1fr);}.inspector-card", shadowSelector, " {grid-column: 1 / -1;}}@media (max-width: 980px) {.plan-layout", shadowSelector, ", .governance-grid", shadowSelector, ", .review-stats", shadowSelector, ", .json-grid", shadowSelector, ", .dag-groups", shadowSelector, ", .visibility-grid", shadowSelector, ", .binding-row", shadowSelector, " dl", shadowSelector, " {grid-template-columns: 1fr;}}"].join('');
  /*LWC compiler v9.2.2*/
}
var _implicitStylesheets$2 = [stylesheet$2];

const $fragment1$3 = parseFragment`<div class="slds-card__header slds-grid${0}"${2}><header class="slds-media slds-media_center slds-has-flexi-truncate${0}"${2}><div class="slds-media__body${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Request</h2><p class="slds-text-body_small slds-text-color_weak${0}"${2}>Scenario, goal, connection readiness</p></div></header><span class="slds-badge${0}"${2}>${"t8"}</span></div>`;
const $fragment2$3 = parseFragment`<label class="slds-form-element__label${0}"${"a0:for"}${2}>Scenario</label>`;
const $fragment3$3 = parseFragment`<option${"a0:value"}${"a0:selected"}${3}>${"t1"}</option>`;
const $fragment4$3 = parseFragment`<div class="slds-form-element${0}"${2}><label class="slds-form-element__label${0}"${"a1:for"}${2}>Goal</label><div class="slds-form-element__control${0}"${2}><textarea${"a4:id"} class="slds-textarea${0}"${2}>${"t5"}</textarea></div></div>`;
const $fragment5$3 = parseFragment`<div class="request-actions slds-m-top_medium${0}"${2}><button class="slds-button slds-button_brand${0}"${"a1:disabled"}${2}>${"t2"}</button><button class="slds-button slds-button_neutral${0}"${"a3:disabled"}${2}>${"t4"}</button><button class="slds-button slds-button_neutral${0}"${"a5:disabled"}${2}>${"t6"}</button><button class="slds-button slds-button_neutral${0}"${"a7:disabled"}${2}>${"t8"}</button><button class="slds-button slds-button_neutral${0}"${"a9:disabled"}${2}>${"t10"}</button></div>`;
const $fragment6$3 = parseFragment`<header class="slds-media slds-media_center slds-has-flexi-truncate${0}"${2}><div class="slds-media__body${0}"${2}><h2 class="slds-card__header-title${0}"${2}>PlanSpec</h2><p class="slds-text-body_small slds-text-color_weak${0}"${2}>Governed ASL profile before any Data 360 mutation</p></div></header>`;
const $fragment7$3 = parseFragment`<span${"c0"}${2}>${"t1"}</span>`;
const $fragment8$2 = parseFragment`<span${"c0"}${2}>${"t1"}</span>`;
const $fragment9$2 = parseFragment`<div class="empty-state${0}"${2}>No plan drafted.</div>`;
const $fragment10$2 = parseFragment`<div class="plan-head${0}"${2}><div${3}><strong${3}>${"t3"}</strong><p${3}>${"t5"}</p></div><span class="slds-badge${0}"${2}>${"t7"}</span></div>`;
const $fragment11$2 = parseFragment`<div${3}><strong${3}>${"t2"}</strong><span${3}>${"t4"}</span></div>`;
const $fragment12$2 = parseFragment`<article${"c0"}${2}><span${3}>${"t2"}</span><strong${3}>${"t4"}</strong><p${3}>${"t6"}</p></article>`;
const $fragment13$2 = parseFragment`<div${"c0"}${2}>${"t1"}</div>`;
const $fragment14$2 = parseFragment`<div class="slds-notify slds-notify_alert validation-ok${0}" role="status"${2}><span${3}>No blocking issues found.</span></div>`;
const $fragment15$2 = parseFragment`<span${"c0"}${"a0:title"}${2}></span>`;
const $fragment16$1 = parseFragment`<button${"c0"}${"a0:data-view"}${2}>${"t1"}</button>`;
const $fragment17$1 = parseFragment`<thead${3}><tr${3}><th scope="col"${3}>Step</th><th scope="col"${3}>Capability</th><th scope="col"${3}>Phase</th><th scope="col"${3}>Status</th><th scope="col"${3}>Gate</th><th scope="col"${3}></th></tr></thead>`;
const $fragment18$1 = parseFragment`<th scope="row"${3}><div class="slds-truncate${0}"${"a1:title"}${2}>${"t2"}</div><span${3}>${"t4"}</span></th>`;
const $fragment19$1 = parseFragment`<td${3}>${"t1"}</td>`;
const $fragment20$1 = parseFragment`<td${3}>${"t1"}</td>`;
const $fragment21$1 = parseFragment`<td${3}><span${"c1"}${2}>${"t2"}</span></td>`;
const $fragment22$1 = parseFragment`<td${3}><span${"c1"}${2}>${"t2"}</span></td>`;
const $fragment23$1 = parseFragment`<button class="slds-button slds-button_brand slds-button_x-small${0}"${"a0:data-step-id"}${2}>Approve</button>`;
const $fragment24$1 = parseFragment`<div class="empty-state${0}"${2}>No DAG artifact available.</div>`;
const $fragment25$1 = parseFragment`<div class="section-header${0}"${2}><h3${3}>${"t2"}</h3><span${3}>${"t4"}</span></div>`;
const $fragment26$1 = parseFragment`<h3${3}>${"t1"}</h3>`;
const $fragment27$1 = parseFragment`<span class="dag-node-title${0}"${2}>${"t1"}</span>`;
const $fragment28$1 = parseFragment`<span class="dag-node-meta${0}"${2}>${"t1"}</span>`;
const $fragment29$1 = parseFragment`<span class="dag-node-footer${0}"${2}><span${3}>${"t2"}</span><span${3}>${"t4"}</span><span${3}>${"t6"}</span></span>`;
const $fragment30$1 = parseFragment`<span class="dag-warning${0}"${2}>${"t1"}</span>`;
const $fragment31$1 = parseFragment`<strong${3}>${"t1"}</strong>`;
const $fragment32$1 = parseFragment`<span${3}>${"t1"}</span>`;
const $fragment33$1 = parseFragment`<strong${3}>${"t1"}</strong>`;
const $fragment34$1 = parseFragment`<small${3}>${"t1"}</small>`;
const $fragment35$1 = parseFragment`<div class="section-header${0}"${2}><h3${3}>Registry drift</h3><span${3}>Frozen bindings</span></div>`;
const $fragment36$1 = parseFragment`<div${"c0"}${2}><strong${3}>${"t2"}</strong><span${3}>${"t4"}</span><p${3}>${"t6"}</p></div>`;
const $fragment37$1 = parseFragment`<p class="slds-text-body_small slds-text-color_weak${0}"${2}>${"t1"}</p>`;
const $fragment38$1 = parseFragment`<article${"c0"}${2}><div${3}><strong${3}>${"t3"}</strong><span${3}>${"t5"}</span></div><dl${3}><div${3}><dt${3}>Effect</dt><dd${3}>${"t11"}</dd></div><div${3}><dt${3}>Gate</dt><dd${3}>${"t16"}</dd></div><div${3}><dt${3}>Schema</dt><dd${3}>${"t21"}</dd></div><div${3}><dt${3}>Registry</dt><dd${3}>${"t26"}</dd></div><div${3}><dt${3}>Tool</dt><dd${3}>${"t31"}</dd></div><div${3}><dt${3}>Connector</dt><dd${3}>${"t36"}</dd></div></dl><span${3}>${"t38"}</span></article>`;
const $fragment39$1 = parseFragment`<div class="section-header${0}"${2}><h3${3}>Redaction</h3><span${3}>Export and traces</span></div>`;
const $fragment40$1 = parseFragment`<div${"c0"}${2}><strong${3}>${"t2"}</strong><span${3}>${"t4"}</span><p${3}>${"t6"}</p></div>`;
const $fragment41 = parseFragment`<h3${3}>Approvals</h3>`;
const $fragment42 = parseFragment`<div class="history-row plan-approval${0}"${2}><strong${3}>${"t2"}</strong><span${3}>${"t4"}</span><p${3}>${"t6"}</p></div>`;
const $fragment43 = parseFragment`<div class="history-row${0}"${2}><strong${3}>${"t2"}</strong><span${3}>${"t4"}</span><p${3}>${"t6"}</p></div>`;
const $fragment44 = parseFragment`<p class="slds-text-body_small slds-text-color_weak${0}"${2}>${"t1"}</p>`;
const $fragment45 = parseFragment`<div class="slds-card__header slds-grid${0}"${2}><header class="slds-media slds-media_center slds-has-flexi-truncate${0}"${2}><div class="slds-media__body${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Step Inspector</h2><p class="slds-text-body_small slds-text-color_weak${0}"${2}>Inputs, bindings, outputs</p></div></header></div>`;
const $fragment46 = parseFragment`<div class="empty-state${0}"${2}>Select a plan step.</div>`;
const $fragment47 = parseFragment`<div class="inspector-head${0}"${2}><strong${3}>${"t2"}</strong><span class="slds-badge${0}"${2}>${"t4"}</span></div>`;
const $fragment48 = parseFragment`<dl class="inspector-list${0}"${2}><div${3}><dt${3}>Step</dt><dd${3}>${"t5"}</dd></div><div${3}><dt${3}>Phase</dt><dd${3}>${"t10"}</dd></div><div${3}><dt${3}>Action</dt><dd${3}>${"t15"}</dd></div><div${3}><dt${3}>ResultPath</dt><dd${3}>${"t20"}</dd></div><div${3}><dt${3}>Approval</dt><dd${3}>${"t25"}</dd></div><div${3}><dt${3}>Timing</dt><dd${3}>${"t30"}</dd></div></dl>`;
const $fragment49 = parseFragment`<article class="json-preview${0}"${2}><strong${3}>${"t2"}</strong><pre${3}>${"t4"}</pre></article>`;
const stc0$3 = {
  classMap: {
    "plan-layout": true
  },
  key: 0
};
const stc1$3 = {
  classMap: {
    "slds-card": true,
    "request-card": true
  },
  key: 1
};
const stc2$3 = {
  classMap: {
    "slds-card__body": true,
    "slds-card__body_inner": true
  },
  key: 4
};
const stc3$3 = {
  classMap: {
    "slds-form-element": true,
    "slds-m-bottom_small": true
  },
  key: 5
};
const stc4$3 = {
  classMap: {
    "slds-form-element__control": true
  },
  key: 8
};
const stc5$2 = {
  "slds-select": true
};
const stc6$2 = {
  classMap: {
    "slds-card": true,
    "plan-card": true
  },
  key: 16
};
const stc7$2 = {
  classMap: {
    "slds-card__header": true,
    "slds-grid": true
  },
  key: 17
};
const stc8$2 = {
  classMap: {
    "slds-card__body": true,
    "slds-card__body_inner": true
  },
  key: 24
};
const stc9$2 = {
  classMap: {
    "review-stats": true
  },
  key: 29
};
const stc10$1 = {
  classMap: {
    "governance-grid": true
  },
  attrs: {
    "aria-label": "Plan governance"
  },
  key: 32
};
const stc11$1 = {
  classMap: {
    "slds-m-vertical_small": true,
    "issue-stack": true
  },
  key: 35
};
const stc12$1 = {
  classMap: {
    "timeline": true
  },
  attrs: {
    "aria-label": "Run timeline"
  },
  key: 40
};
const stc13$1 = {
  classMap: {
    "plan-view-tabs": true
  },
  attrs: {
    "role": "tablist",
    "aria-label": "Plan views"
  },
  key: 43
};
const stc14$1 = {
  classMap: {
    "table-wrap": true
  },
  key: 46
};
const stc15$1 = {
  classMap: {
    "slds-table": true,
    "slds-table_cell-buffer": true,
    "slds-table_bordered": true,
    "plan-table": true
  },
  key: 47
};
const stc16$1 = {
  key: 50
};
const stc17$1 = {
  key: 62
};
const stc18$1 = {
  classMap: {
    "dag-panel": true
  },
  attrs: {
    "aria-label": "Plan DAG"
  },
  key: 65
};
const stc19$1 = {
  classMap: {
    "dag-groups": true
  },
  key: 70
};
const stc20$1 = {
  "dag-group": true
};
const stc21$1 = {
  classMap: {
    "dag-edge-list": true
  },
  attrs: {
    "aria-label": "DAG edges"
  },
  key: 83
};
const stc22$1 = {
  classMap: {
    "visibility-grid": true
  },
  key: 93
};
const stc23$1 = {
  classMap: {
    "visibility-panel": true,
    "registry-panel": true
  },
  attrs: {
    "aria-label": "Registry drift"
  },
  key: 94
};
const stc24$1 = {
  classMap: {
    "signal-stack": true
  },
  key: 97
};
const stc25$1 = {
  classMap: {
    "binding-list": true
  },
  key: 102
};
const stc26$1 = {
  classMap: {
    "visibility-panel": true
  },
  attrs: {
    "aria-label": "Redaction warnings"
  },
  key: 105
};
const stc27$1 = {
  classMap: {
    "signal-stack": true
  },
  key: 108
};
const stc28$1 = {
  classMap: {
    "approval-history": true
  },
  key: 111
};
const stc29$1 = [];
const stc30$1 = {
  classMap: {
    "slds-card": true,
    "inspector-card": true
  },
  key: 120
};
const stc31$1 = {
  classMap: {
    "slds-card__body": true,
    "slds-card__body_inner": true
  },
  key: 123
};
const stc32$1 = {
  classMap: {
    "json-grid": true
  },
  key: 130
};
function tmpl$3($api, $cmp, $slotset, $ctx) {
  const {d: api_dynamic_text, sp: api_static_part, st: api_static_fragment, gid: api_scoped_id, b: api_bind, k: api_key, i: api_iterator, h: api_element, ncls: api_normalize_class_name, f: api_flatten} = $api;
  const {_m0, _m1, _m2, _m3, _m4, _m5, _m6, _m7, _m8, _m9, _m10, _m11, _m12} = $ctx;
  return [api_element("div", stc0$3, [api_element("section", stc1$3, [api_static_fragment($fragment1$3, 3, [api_static_part(8, null, api_dynamic_text($cmp.plannerStatus))]), api_element("div", stc2$3, [api_element("div", stc3$3, [api_static_fragment($fragment2$3, 7, [api_static_part(0, {
    attrs: {
      "for": api_scoped_id("scenario")
    }
  }, null)]), api_element("div", stc4$3, [api_element("select", {
    classMap: stc5$2,
    attrs: {
      "id": api_scoped_id("scenario")
    },
    key: 9,
    on: _m0 || ($ctx._m0 = {
      "change": api_bind($cmp.handleScenarioChange)
    })
  }, api_iterator($cmp.scenarioOptions, function (scenario) {
    return api_static_fragment($fragment3$3, api_key(11, scenario.id), [api_static_part(0, {
      attrs: {
        "value": scenario.id,
        "selected": scenario.selected ? "" : null
      }
    }, null), api_static_part(1, null, api_dynamic_text(scenario.name))]);
  }))])]), api_static_fragment($fragment4$3, 13, [api_static_part(1, {
    attrs: {
      "for": api_scoped_id("goal")
    }
  }, null), api_static_part(4, {
    on: _m1 || ($ctx._m1 = {
      "input": api_bind($cmp.handleGoalChange)
    }),
    attrs: {
      "id": api_scoped_id("goal")
    }
  }, null), api_static_part(5, null, api_dynamic_text($cmp.goal))]), api_static_fragment($fragment5$3, 15, [api_static_part(1, {
    on: _m2 || ($ctx._m2 = {
      "click": api_bind($cmp.handleDraft)
    }),
    attrs: {
      "disabled": $cmp.busy.draftPlan ? "" : null
    }
  }, null), api_static_part(2, null, api_dynamic_text($cmp.draftLabel)), api_static_part(3, {
    on: _m3 || ($ctx._m3 = {
      "click": api_bind($cmp.handleApprovePlan)
    }),
    attrs: {
      "disabled": $cmp.approveDisabled ? "" : null
    }
  }, null), api_static_part(4, null, api_dynamic_text($cmp.approveLabel)), api_static_part(5, {
    on: _m4 || ($ctx._m4 = {
      "click": api_bind($cmp.handleStart)
    }),
    attrs: {
      "disabled": $cmp.startDisabled ? "" : null
    }
  }, null), api_static_part(6, null, api_dynamic_text($cmp.startLabel)), api_static_part(7, {
    on: _m5 || ($ctx._m5 = {
      "click": api_bind($cmp.handleExportPlanSpec)
    }),
    attrs: {
      "disabled": $cmp.exportDisabled ? "" : null
    }
  }, null), api_static_part(8, null, api_dynamic_text($cmp.exportLabel)), api_static_part(9, {
    on: _m6 || ($ctx._m6 = {
      "click": api_bind($cmp.handleSmoke)
    }),
    attrs: {
      "disabled": $cmp.busy.smokeData360 ? "" : null
    }
  }, null), api_static_part(10, null, api_dynamic_text($cmp.smokeLabel))])])]), api_element("section", stc6$2, [api_element("div", stc7$2, [api_static_fragment($fragment6$3, 19), $cmp.hasDraft ? api_static_fragment($fragment7$3, 21, [api_static_part(0, {
    className: api_normalize_class_name($cmp.validationClass)
  }, null), api_static_part(1, null, api_dynamic_text($cmp.validationLabel))]) : null, $cmp.hasDraft ? api_static_fragment($fragment8$2, 23, [api_static_part(0, {
    className: api_normalize_class_name($cmp.approvalClass)
  }, null), api_static_part(1, null, api_dynamic_text($cmp.approvalLabel))]) : null]), api_element("div", stc8$2, [!$cmp.hasDraft ? api_static_fragment($fragment9$2, 26) : null, $cmp.hasDraft ? api_static_fragment($fragment10$2, 28, [api_static_part(3, null, api_dynamic_text($cmp.plan.goal)), api_static_part(5, null, api_dynamic_text($cmp.planContext)), api_static_part(7, null, api_dynamic_text($cmp.plan.scenarioId))]) : null, $cmp.hasDraft ? api_element("div", stc9$2, api_iterator($cmp.reviewStats, function (stat) {
    return api_static_fragment($fragment11$2, api_key(31, stat.label), [api_static_part(2, null, api_dynamic_text(stat.value)), api_static_part(4, null, api_dynamic_text(stat.label))]);
  })) : null, $cmp.hasDraft ? api_element("div", stc10$1, api_iterator($cmp.governanceCards, function (card) {
    return api_static_fragment($fragment12$2, api_key(34, card.label), [api_static_part(0, {
      className: api_normalize_class_name(card.className)
    }, null), api_static_part(2, null, api_dynamic_text(card.label)), api_static_part(4, null, api_dynamic_text(card.value)), api_static_part(6, null, api_dynamic_text(card.detail))]);
  })) : null, $cmp.hasDraft ? $cmp.hasIssues ? api_element("div", stc11$1, api_iterator($cmp.issues, function (issue) {
    return api_static_fragment($fragment13$2, api_key(37, issue.key), [api_static_part(0, {
      className: api_normalize_class_name(issue.className)
    }, null), api_static_part(1, null, api_dynamic_text(issue.severity) + " • " + api_dynamic_text(issue.stepId) + " • " + api_dynamic_text(issue.message))]);
  })) : null : null, $cmp.hasDraft ? !$cmp.hasIssues ? api_static_fragment($fragment14$2, 39) : null : null, $cmp.hasDraft ? $cmp.hasRun ? api_element("div", stc12$1, api_iterator($cmp.timelineSteps, function (step) {
    return api_static_fragment($fragment15$2, api_key(42, step.key), [api_static_part(0, {
      className: api_normalize_class_name(step.className),
      attrs: {
        "title": step.status
      }
    }, null)]);
  })) : null : null, $cmp.hasDraft ? api_element("div", stc13$1, api_iterator($cmp.planViewOptions, function (option) {
    return api_static_fragment($fragment16$1, api_key(45, option.key), [api_static_part(0, {
      on: _m8 || ($ctx._m8 = {
        "click": api_bind($cmp.handlePlanView)
      }),
      className: api_normalize_class_name(option.className),
      attrs: {
        "data-view": option.key
      }
    }, null), api_static_part(1, null, api_dynamic_text(option.label))]);
  })) : null, $cmp.hasDraft ? $cmp.showStepsView ? api_element("div", stc14$1, [api_element("table", stc15$1, [api_static_fragment($fragment17$1, 49), api_element("tbody", stc16$1, api_iterator($cmp.planRows, function (row) {
    return api_element("tr", {
      className: api_normalize_class_name(row.rowClass),
      attrs: {
        "tabindex": "0",
        "data-step-id": row.id
      },
      key: api_key(51, row.key),
      on: _m9 || ($ctx._m9 = {
        "click": api_bind($cmp.handleSelectStep),
        "keydown": api_bind($cmp.handleStepKeydown)
      })
    }, [api_static_fragment($fragment18$1, 53, [api_static_part(1, {
      attrs: {
        "title": row.title
      }
    }, null), api_static_part(2, null, api_dynamic_text(row.title)), api_static_part(4, null, api_dynamic_text(row.id))]), api_static_fragment($fragment19$1, 55, [api_static_part(1, null, api_dynamic_text(row.resource))]), api_static_fragment($fragment20$1, 57, [api_static_part(1, null, api_dynamic_text(row.phase))]), api_static_fragment($fragment21$1, 59, [api_static_part(1, {
      className: api_normalize_class_name(row.statusClass)
    }, null), api_static_part(2, null, api_dynamic_text(row.status))]), api_static_fragment($fragment22$1, 61, [api_static_part(1, {
      className: api_normalize_class_name(row.approvalClass)
    }, null), api_static_part(2, null, api_dynamic_text(row.approvalLabel))]), api_element("td", stc17$1, [row.canApprove ? api_static_fragment($fragment23$1, 64, [api_static_part(0, {
      on: _m11 || ($ctx._m11 = {
        "click": api_bind($cmp.handleApproveStep)
      }),
      attrs: {
        "data-step-id": row.id
      }
    }, null)]) : null])]);
  }))])]) : null : null, $cmp.hasDraft ? $cmp.showDagView ? api_element("section", stc18$1, [!$cmp.hasDag ? api_static_fragment($fragment24$1, 67) : null, $cmp.hasDag ? api_static_fragment($fragment25$1, 69, [api_static_part(2, null, api_dynamic_text($cmp.dagSourceLabel)), api_static_part(4, null, api_dynamic_text($cmp.dagOrderCount) + " nodes ordered")]) : null, $cmp.hasDag ? api_element("div", stc19$1, api_iterator($cmp.dagRows, function (group) {
    return api_element("section", {
      classMap: stc20$1,
      key: api_key(71, group.id)
    }, api_flatten([api_static_fragment($fragment26$1, 73, [api_static_part(1, null, api_dynamic_text(group.label))]), api_iterator(group.nodes, function (node) {
      return api_element("button", {
        className: api_normalize_class_name(node.className),
        attrs: {
          "data-step-id": node.id
        },
        key: api_key(74, node.id),
        on: _m12 || ($ctx._m12 = {
          "click": api_bind($cmp.handleDagNode)
        })
      }, [api_static_fragment($fragment27$1, 76, [api_static_part(1, null, api_dynamic_text(node.label))]), api_static_fragment($fragment28$1, 78, [api_static_part(1, null, api_dynamic_text(node.action))]), api_static_fragment($fragment29$1, 80, [api_static_part(2, null, api_dynamic_text(node.effectLabel)), api_static_part(4, null, api_dynamic_text(node.gateLabel)), api_static_part(6, null, api_dynamic_text(node.status))]), node.hasWarnings ? api_static_fragment($fragment30$1, 82, [api_static_part(1, null, api_dynamic_text(node.warningText))]) : null]);
    })]));
  })) : null, $cmp.hasDag ? api_element("div", stc21$1, api_iterator($cmp.dagEdges, function (edge) {
    return api_element("div", {
      className: api_normalize_class_name(edge.className),
      key: api_key(84, edge.key)
    }, [api_static_fragment($fragment31$1, 86, [api_static_part(1, null, api_dynamic_text(edge.from))]), api_static_fragment($fragment32$1, 88, [api_static_part(1, null, api_dynamic_text(edge.type))]), api_static_fragment($fragment33$1, 90, [api_static_part(1, null, api_dynamic_text(edge.to))]), edge.label ? api_static_fragment($fragment34$1, 92, [api_static_part(1, null, api_dynamic_text(edge.label))]) : null]);
  })) : null]) : null : null, $cmp.hasDraft ? api_element("div", stc22$1, [api_element("section", stc23$1, [api_static_fragment($fragment35$1, 96), $cmp.hasRegistryDriftSignals ? api_element("div", stc24$1, api_iterator($cmp.registryDriftSignals, function (signal) {
    return api_static_fragment($fragment36$1, api_key(99, signal.key), [api_static_part(0, {
      className: api_normalize_class_name(signal.className)
    }, null), api_static_part(2, null, api_dynamic_text(signal.source)), api_static_part(4, null, api_dynamic_text(signal.status)), api_static_part(6, null, api_dynamic_text(signal.detail))]);
  })) : null, !$cmp.hasRegistryDriftSignals ? api_static_fragment($fragment37$1, 101, [api_static_part(1, null, api_dynamic_text($cmp.registryFallback))]) : null, $cmp.hasBindingRows ? api_element("div", stc25$1, api_iterator($cmp.bindingRows, function (binding) {
    return api_static_fragment($fragment38$1, api_key(104, binding.key), [api_static_part(0, {
      className: api_normalize_class_name(binding.className)
    }, null), api_static_part(3, null, api_dynamic_text(binding.resource)), api_static_part(5, null, api_dynamic_text(binding.tool)), api_static_part(11, null, api_dynamic_text(binding.effect)), api_static_part(16, null, api_dynamic_text(binding.approval)), api_static_part(21, null, api_dynamic_text(binding.schemaHash)), api_static_part(26, null, api_dynamic_text(binding.registryHash)), api_static_part(31, null, api_dynamic_text(binding.toolSchemaHash)), api_static_part(36, null, api_dynamic_text(binding.connectorDefinitionHash)), api_static_part(38, null, api_dynamic_text(binding.statusLabel))]);
  })) : null]), api_element("section", stc26$1, [api_static_fragment($fragment39$1, 107), $cmp.hasRedactionRows ? api_element("div", stc27$1, api_iterator($cmp.redactionRows, function (row) {
    return api_static_fragment($fragment40$1, api_key(110, row.key), [api_static_part(0, {
      className: api_normalize_class_name(row.className)
    }, null), api_static_part(2, null, api_dynamic_text(row.scope)), api_static_part(4, null, api_dynamic_text(row.status)), api_static_part(6, null, api_dynamic_text(row.detail))]);
  })) : null])]) : null, $cmp.hasDraft ? api_element("section", stc28$1, api_flatten([api_static_fragment($fragment41, 113), $cmp.hasApprovedArtifactRows ? api_iterator($cmp.approvedArtifactRows, function (item) {
    return api_static_fragment($fragment42, api_key(115, item.key), [api_static_part(2, null, api_dynamic_text(item.step)), api_static_part(4, null, api_dynamic_text(item.status)), api_static_part(6, null, api_dynamic_text(item.detail))]);
  }) : stc29$1, $cmp.hasApprovalRows ? api_iterator($cmp.approvalRows, function (item) {
    return api_static_fragment($fragment43, api_key(117, item.key), [api_static_part(2, null, api_dynamic_text(item.step)), api_static_part(4, null, api_dynamic_text(item.status)), api_static_part(6, null, api_dynamic_text(item.detail))]);
  }) : stc29$1, $cmp.showApprovalFallback ? api_static_fragment($fragment44, 119, [api_static_part(1, null, api_dynamic_text($cmp.approvalFallback))]) : null])) : null])]), api_element("aside", stc30$1, [api_static_fragment($fragment45, 122), api_element("div", stc31$1, [!$cmp.hasSelectedStep ? api_static_fragment($fragment46, 125) : null, $cmp.hasSelectedStep ? api_static_fragment($fragment47, 127, [api_static_part(2, null, api_dynamic_text($cmp.selectedStep.title)), api_static_part(4, null, api_dynamic_text($cmp.selectedStatus))]) : null, $cmp.hasSelectedStep ? api_static_fragment($fragment48, 129, [api_static_part(5, null, api_dynamic_text($cmp.selectedStep.id)), api_static_part(10, null, api_dynamic_text($cmp.selectedStep.phase)), api_static_part(15, null, api_dynamic_text($cmp.selectedStep.action)), api_static_part(20, null, api_dynamic_text($cmp.selectedStep.resultPath)), api_static_part(25, null, api_dynamic_text($cmp.selectedApproval)), api_static_part(30, null, api_dynamic_text($cmp.selectedTiming))]) : null, $cmp.hasSelectedStep ? api_element("div", stc32$1, api_iterator($cmp.selectedPreviews, function (preview) {
    return api_static_fragment($fragment49, api_key(132, preview.label), [api_static_part(2, null, api_dynamic_text(preview.label)), api_static_part(4, null, api_dynamic_text(preview.value))]);
  })) : null])])])];
  /*LWC compiler v9.2.2*/
}
var _tmpl$3 = registerTemplate(tmpl$3);
tmpl$3.stylesheets = [];
tmpl$3.stylesheetToken = "lwc-7rcr1t6e1pr";
tmpl$3.legacyStylesheetToken = "c-planWorkspace_planWorkspace";
if (_implicitStylesheets$2) {
  tmpl$3.stylesheets.push.apply(tmpl$3.stylesheets, _implicitStylesheets$2);
}
freezeTemplate(tmpl$3);

class PlanWorkspace extends LightningElement {
  constructor(...args) {
    super(...args);
    this.scenarios = [];
    this.selectedScenarioId = "";
    this.goal = "";
    this.diagnostics = void 0;
    this.plannerStatus = "Ready";
    this.currentDraft = void 0;
    this.approvedPlan = void 0;
    this.currentRun = void 0;
    this.approvalHistory = [];
    this.approvalHistoryUnavailable = void 0;
    this.selectedStepId = void 0;
    this.busy = {};
    this.activePlanView = "steps";
  }
  get hasDraft() {
    return Boolean(this.currentDraft?.plan);
  }
  get scenarioOptions() {
    return (this.scenarios || []).map(scenario => ({
      ...scenario,
      selected: scenario.id === this.selectedScenarioId
    }));
  }
  get startDisabled() {
    return !this.approvedPlan || this.busy.startPlan || this.busy.approvePlan;
  }
  get exportDisabled() {
    return !this.currentDraft?.plan?.id || this.busy.exportPlanSpec;
  }
  get approveDisabled() {
    return !this.currentDraft?.validation?.ok || this.busy.approvePlan || Boolean(this.approvedPlan);
  }
  get draftLabel() {
    return this.busy.draftPlan ? "Drafting..." : "Draft plan";
  }
  get startLabel() {
    return this.busy.startPlan ? "Starting..." : "Start run";
  }
  get exportLabel() {
    return this.busy.exportPlanSpec ? "Exporting..." : "Export PlanSpec";
  }
  get approveLabel() {
    if (this.busy.approvePlan) return "Approving...";
    return this.approvedPlan ? "Approved" : "Approve plan";
  }
  get smokeLabel() {
    return this.busy.smokeData360 ? "Testing..." : "Test connection";
  }
  get plan() {
    return this.currentDraft?.plan;
  }
  get planContext() {
    const context = this.plan?.context || {};
    return [context.org ? `org ${context.org}` : null, context.dataspace ? `dataspace ${context.dataspace}` : null, context.environment].filter(Boolean).join(" • ") || "No context supplied";
  }
  get validationClass() {
    return this.currentDraft?.validation?.ok ? "slds-badge slds-theme_success" : "slds-badge slds-theme_warning";
  }
  get validationLabel() {
    return this.currentDraft?.validation?.ok ? "Validated" : "Needs review";
  }
  get approvalClass() {
    return this.approvedPlan ? "slds-badge slds-theme_success" : "slds-badge";
  }
  get approvalLabel() {
    return this.approvedPlan ? "Executable artifact approved" : "Awaiting plan approval";
  }
  get activeArtifacts() {
    return this.approvedPlan?.artifacts?.length ? this.approvedPlan.artifacts : this.currentDraft?.artifacts || [];
  }
  get activeOperationBindings() {
    if (this.approvedPlan?.operationBindings?.length) return this.approvedPlan.operationBindings;
    if (this.currentRun?.operationBindings?.length) return this.currentRun.operationBindings;
    return this.currentDraft?.operationBindings || [];
  }
  get artifactSummary() {
    if (this.approvedPlan) {
      const detail = [this.approvedPlan.approvedBy ? `by ${this.approvedPlan.approvedBy}` : null, this.approvedPlan.approvedAt ? dateTime(this.approvedPlan.approvedAt) : null, shortHash(this.approvedPlan.planHash)].filter(Boolean).join(" • ");
      return {
        label: "Approved",
        value: this.approvedPlan.artifactId || "approved artifact",
        detail: detail || "Executable artifact frozen",
        className: "governance-card good"
      };
    }
    return {
      label: "Draft",
      value: this.currentDraft?.plan?.id || "No plan",
      detail: "Approve to freeze executable metadata",
      className: "governance-card neutral"
    };
  }
  get approvalSummary() {
    const required = this.activeOperationBindings.filter(binding => binding.requiresApproval).length || (this.plan?.steps || []).filter(step => step.needsApproval).length;
    if (this.approvedPlan) {
      return {
        label: "Approval",
        value: "Approved",
        detail: `${number(required)} gated ${required === 1 ? "operation" : "operations"}`,
        className: "governance-card good"
      };
    }
    return {
      label: "Approval",
      value: "Pending",
      detail: `${number(required)} gated ${required === 1 ? "operation" : "operations"}`,
      className: "governance-card warning"
    };
  }
  get registrySummary() {
    if (this.hasRegistryDriftSignals) {
      return {
        label: "Registry",
        value: "Review drift",
        detail: `${number(this.registryDriftSignals.length)} registry ${this.registryDriftSignals.length === 1 ? "signal" : "signals"}`,
        className: "governance-card error"
      };
    }
    const frozen = this.activeOperationBindings.filter(binding => binding.registryHash || binding.toolSchemaHash).length;
    if (frozen > 0) {
      return {
        label: "Registry",
        value: "Frozen",
        detail: `${number(frozen)} MCP ${frozen === 1 ? "binding" : "bindings"} with hashes`,
        className: "governance-card good"
      };
    }
    return {
      label: "Registry",
      value: "Catalog only",
      detail: "No live MCP registry hash in this artifact",
      className: "governance-card neutral"
    };
  }
  get redactionSummary() {
    const detected = this.redactionRows.filter(row => row.detected).length;
    return {
      label: "Redaction",
      value: detected ? `${number(detected)} detected` : "Export guarded",
      detail: detected ? "Sensitive or truncated values are hidden" : "Exports use the redacted archive payload",
      className: detected ? "governance-card warning" : "governance-card good"
    };
  }
  get governanceCards() {
    return [this.artifactSummary, this.approvalSummary, this.registrySummary, this.redactionSummary];
  }
  get reviewStats() {
    const steps = this.plan?.steps || [];
    const states = Object.keys(this.plan?.definition?.States || {});
    return [{
      label: "ASL states",
      value: number(states.length || steps.length)
    }, {
      label: "Approval gates",
      value: number(steps.filter(step => step.needsApproval).length)
    }, {
      label: "StartAt",
      value: this.plan?.definition?.StartAt || "n/a"
    }, {
      label: "Monitors",
      value: number(steps.filter(step => slug(step.phase) === "monitor").length)
    }];
  }
  get issues() {
    return (this.currentDraft?.validation?.issues || []).map(issue => ({
      ...issue,
      key: `${issue.stepId || "plan"}-${issue.message}`,
      className: `slds-box slds-theme_alert-texture issue ${slug(issue.severity)}`
    }));
  }
  get hasIssues() {
    return this.issues.length > 0;
  }
  get runStepMap() {
    return new Map((this.currentRun?.steps || []).map(step => [step.stepId, step]));
  }
  get planRows() {
    return (this.plan?.steps || []).map((step, index) => {
      const runStep = this.runStepMap.get(step.id);
      const aslState = this.plan?.definition?.States?.[step.id] || {};
      const status = runStep?.status || "PENDING";
      return {
        ...step,
        resource: aslState.Resource || step.action,
        resultPath: aslState.ResultPath || `$.${step.id}`,
        index: index + 1,
        status,
        key: step.id,
        rowClass: `plan-row ${this.selectedStepId === step.id ? "selected" : ""}`,
        statusClass: `slds-badge status ${slug(status)}`,
        approvalClass: `slds-badge ${step.needsApproval ? "slds-theme_warning" : ""}`,
        approvalLabel: step.needsApproval ? "Approval" : "Auto",
        canApprove: status === "WAITING_APPROVAL"
      };
    });
  }
  get planViewOptions() {
    return [{
      key: "steps",
      label: "Steps",
      className: this.activePlanView === "steps" ? "view-tab active" : "view-tab"
    }, {
      key: "dag",
      label: "DAG",
      className: this.activePlanView === "dag" ? "view-tab active" : "view-tab"
    }];
  }
  get showStepsView() {
    return this.activePlanView === "steps";
  }
  get showDagView() {
    return this.activePlanView === "dag";
  }
  get planDag() {
    return this.activeArtifacts.find(artifact => artifact.type === "plan_dag")?.data || {};
  }
  get dagRows() {
    const nodes = this.planDag.nodes || [];
    const groups = this.planDag.groups?.length ? this.planDag.groups : [{
      id: "discover",
      label: "Discover",
      nodeIds: nodes.filter(node => slug(node.phase) === "discover").map(node => node.id)
    }, {
      id: "setup",
      label: "Setup",
      nodeIds: nodes.filter(node => slug(node.phase) === "setup").map(node => node.id)
    }, {
      id: "monitor",
      label: "Monitor",
      nodeIds: nodes.filter(node => slug(node.phase) === "monitor").map(node => node.id)
    }];
    const nodeMap = new Map(nodes.map(node => [node.id, node]));
    const warningsByNode = new Map();
    (this.planDag.warnings || []).forEach(warning => {
      const nodeId = warning.nodeId || "plan";
      warningsByNode.set(nodeId, [...(warningsByNode.get(nodeId) || []), warning.message]);
    });
    return groups.map(group => ({
      ...group,
      nodes: (group.nodeIds || []).map(nodeId => this.decorateDagNode(nodeMap.get(nodeId), warningsByNode)).filter(Boolean)
    })).filter(group => group.nodes.length);
  }
  get dagEdges() {
    return (this.planDag.edges || []).map((edge, index) => ({
      ...edge,
      key: `${edge.from}-${edge.to}-${edge.type}-${index}`,
      className: `dag-edge ${slug(edge.type)}`
    }));
  }
  get hasDag() {
    return this.dagRows.length > 0;
  }
  get dagSourceLabel() {
    return this.approvedPlan ? "Approved artifact DAG" : "Draft DAG";
  }
  get dagOrderCount() {
    return number((this.planDag.topologicalOrder || []).length);
  }
  get bindingRows() {
    return this.activeOperationBindings.map((binding, index) => {
      const hasRegistry = Boolean(binding.registryHash || binding.toolSchemaHash || binding.connectorDefinitionHash);
      const tool = [binding.mcpServerId, binding.underlyingTool || binding.facadeTool].filter(Boolean).join(" / ");
      return {
        key: `${binding.resource}-${index}`,
        resource: binding.resource,
        tool: tool || binding.transport || "internal",
        effect: (binding.effect || "READ").toLowerCase(),
        approval: binding.requiresApproval ? "Approval" : "Auto",
        schemaHash: shortHash(binding.schemaHash) || "n/a",
        registryHash: shortHash(binding.registryHash) || "not frozen",
        toolSchemaHash: shortHash(binding.toolSchemaHash) || "not frozen",
        connectorDefinitionHash: shortHash(binding.connectorDefinitionHash) || "not frozen",
        statusLabel: hasRegistry ? "Frozen MCP" : "Local catalog",
        className: `binding-row ${hasRegistry ? "frozen" : "catalog"}`
      };
    });
  }
  get hasBindingRows() {
    return this.bindingRows.length > 0;
  }
  get registryDriftSignals() {
    const issues = (this.currentDraft?.validation?.issues || []).filter(issue => containsRegistryDrift(issue.message)).map((issue, index) => ({
      key: `issue-${issue.stepId || "plan"}-${index}`,
      source: issue.stepId || "plan",
      status: issue.severity || "warning",
      detail: issue.message,
      className: `signal-row ${slug(issue.severity || "warning")}`
    }));
    const runErrors = (this.currentRun?.steps || []).filter(step => containsRegistryDrift(step.error)).map(step => ({
      key: `run-${step.stepId}`,
      source: step.stepId,
      status: step.status || "FAILED",
      detail: step.error,
      className: "signal-row error"
    }));
    return [...issues, ...runErrors];
  }
  get hasRegistryDriftSignals() {
    return this.registryDriftSignals.length > 0;
  }
  get registryFallback() {
    return this.approvedPlan ? "No registry drift reported for this approved artifact. Runtime execution still checks frozen hashes." : "Approve the plan to freeze live MCP registry and tool schema hashes when available.";
  }
  get hasRun() {
    return Boolean(this.currentRun?.steps?.length);
  }
  get timelineSteps() {
    return this.planRows.map(row => ({
      ...row,
      className: `timeline-dot ${slug(row.status)}`
    }));
  }
  get selectedStep() {
    const step = (this.plan?.steps || []).find(item => item.id === this.selectedStepId) || this.plan?.steps?.[0];
    if (!step) return step;
    const aslState = this.plan?.definition?.States?.[step.id] || {};
    return {
      ...step,
      resource: aslState.Resource || step.action,
      resultPath: aslState.ResultPath || `$.${step.id}`
    };
  }
  get selectedRunStep() {
    return this.selectedStep ? this.runStepMap.get(this.selectedStep.id) : null;
  }
  get hasSelectedStep() {
    return Boolean(this.selectedStep);
  }
  get selectedStatus() {
    return this.selectedRunStep?.status || "PENDING";
  }
  get selectedTiming() {
    if (!this.selectedRunStep?.startedAt && !this.selectedRunStep?.finishedAt) return "Not started";
    const started = this.selectedRunStep.startedAt ? `Started ${dateTime(this.selectedRunStep.startedAt)}` : "Not started";
    const finished = this.selectedRunStep.finishedAt ? `Finished ${dateTime(this.selectedRunStep.finishedAt)}` : "In progress";
    return `${started} • ${finished}`;
  }
  get selectedApproval() {
    return this.selectedStep?.needsApproval ? "Required" : "Automatic";
  }
  get selectedPreviews() {
    const aslState = this.plan?.definition?.States?.[this.selectedStep?.id] || {};
    return [{
      label: "ASL State",
      value: toJson(aslState)
    }, {
      label: "Input",
      value: toJson(this.selectedStep?.input || {})
    }, {
      label: "Bindings",
      value: toJson(this.selectedStep?.inputBindings || {})
    }, {
      label: "Resolved Input",
      value: toJson(this.selectedRunStep?.resolvedInput || {})
    }, {
      label: "Output",
      value: toJson(this.selectedRunStep?.output || {})
    }, {
      label: "Raw",
      value: toJson(this.selectedRunStep?.raw || {})
    }];
  }
  get redactionRows() {
    const rows = [{
      key: "export",
      scope: "PlanSpec export",
      status: "Redacted",
      detail: "Export uses the server archive payload with redacted plan artifacts.",
      detected: false,
      className: "signal-row good"
    }];
    const selectedStepPayload = {
      resolvedInput: this.selectedRunStep?.resolvedInput,
      output: this.selectedRunStep?.output,
      raw: this.selectedRunStep?.raw,
      error: this.selectedRunStep?.error
    };
    if (containsRedactionMarker(selectedStepPayload)) {
      rows.push({
        key: `selected-${this.selectedStep?.id || "step"}`,
        scope: this.selectedStep?.id || "Selected step",
        status: "Masked",
        detail: "Selected step data contains redacted or truncated values.",
        detected: true,
        className: "signal-row warning"
      });
    }
    const runMatches = (this.currentRun?.steps || []).filter(step => step.stepId !== this.selectedStep?.id).filter(step => containsRedactionMarker({
      resolvedInput: step.resolvedInput,
      output: step.output,
      raw: step.raw,
      error: step.error
    })).map(step => ({
      key: `run-redaction-${step.stepId}`,
      scope: step.stepId,
      status: "Masked",
      detail: "Run data contains redacted or truncated values.",
      detected: true,
      className: "signal-row warning"
    }));
    return [...rows, ...runMatches];
  }
  get hasRedactionRows() {
    return this.redactionRows.length > 0;
  }
  get approvalRows() {
    const history = this.approvalHistory?.length ? this.approvalHistory : this.currentRun?.approvalHistory || this.currentRun?.approvals || [];
    return history.map((item, index) => ({
      key: `${item.stepId || item.step || "plan"}-${index}`,
      step: item.stepId || item.step || "plan",
      status: item.status || item.decision || "recorded",
      detail: `${item.actor || item.approvedBy || "system"}${item.createdAt || item.approvedAt ? ` • ${dateTime(item.createdAt || item.approvedAt)}` : ""}`
    }));
  }
  get hasApprovalRows() {
    return this.approvalRows.length > 0;
  }
  get approvalFallback() {
    return this.approvalHistoryUnavailable ? `Approval API unavailable: ${this.approvalHistoryUnavailable}` : "No approval records available yet.";
  }
  get showApprovalFallback() {
    return !this.hasApprovalRows && !this.hasApprovedArtifactRows;
  }
  get approvedArtifactRows() {
    if (!this.approvedPlan) return [];
    return [{
      key: this.approvedPlan.artifactId || "approved-plan",
      step: "plan",
      status: "APPROVED",
      detail: [this.approvedPlan.artifactId, this.approvedPlan.approvedBy ? `by ${this.approvedPlan.approvedBy}` : null, this.approvedPlan.approvedAt ? dateTime(this.approvedPlan.approvedAt) : null, shortHash(this.approvedPlan.planHash)].filter(Boolean).join(" • ")
    }];
  }
  get hasApprovedArtifactRows() {
    return this.approvedArtifactRows.length > 0;
  }
  handleScenarioChange(event) {
    this.dispatchEvent(new CustomEvent("scenariochange", {
      detail: {
        scenarioId: event.target.value
      }
    }));
  }
  handleGoalChange(event) {
    this.dispatchEvent(new CustomEvent("goalchange", {
      detail: {
        goal: event.target.value
      }
    }));
  }
  handleDraft() {
    this.dispatchEvent(new CustomEvent("draftplan"));
  }
  handleStart() {
    this.dispatchEvent(new CustomEvent("startplan"));
  }
  handleExportPlanSpec() {
    this.dispatchEvent(new CustomEvent("exportplanspec"));
  }
  handleApprovePlan() {
    this.dispatchEvent(new CustomEvent("approveplan"));
  }
  handleSmoke() {
    this.dispatchEvent(new CustomEvent("smokedata360"));
  }
  handleSelectStep(event) {
    const stepId = event.currentTarget.dataset.stepId;
    this.dispatchEvent(new CustomEvent("selectstep", {
      detail: {
        stepId
      }
    }));
  }
  handlePlanView(event) {
    this.activePlanView = event.currentTarget.dataset.view;
  }
  handleDagNode(event) {
    this.dispatchEvent(new CustomEvent("selectstep", {
      detail: {
        stepId: event.currentTarget.dataset.stepId
      }
    }));
  }
  handleStepKeydown(event) {
    if (!["Enter", " "].includes(event.key)) return;
    event.preventDefault();
    this.handleSelectStep(event);
  }
  handleApproveStep(event) {
    event.stopPropagation();
    this.dispatchEvent(new CustomEvent("approvestep", {
      detail: {
        stepId: event.currentTarget.dataset.stepId
      }
    }));
  }
  decorateDagNode(node, warningsByNode) {
    if (!node) return null;
    const runStep = this.runStepMap.get(node.id);
    const status = runStep?.status || "DRAFT";
    const warnings = warningsByNode.get(node.id) || [];
    return {
      ...node,
      status,
      warnings,
      warningText: warnings.join(" • "),
      hasWarnings: warnings.length > 0,
      className: `dag-node ${slug(node.effect)} ${slug(status)} ${this.selectedStepId === node.id ? "selected" : ""}`,
      effectLabel: node.effect || "read",
      gateLabel: node.approvalRequired ? "Approval" : "Auto"
    };
  }
  /*LWC compiler v9.2.2*/
}
registerDecorators(PlanWorkspace, {
  publicProps: {
    scenarios: {
      config: 0
    },
    selectedScenarioId: {
      config: 0
    },
    goal: {
      config: 0
    },
    diagnostics: {
      config: 0
    },
    plannerStatus: {
      config: 0
    },
    currentDraft: {
      config: 0
    },
    approvedPlan: {
      config: 0
    },
    currentRun: {
      config: 0
    },
    approvalHistory: {
      config: 0
    },
    approvalHistoryUnavailable: {
      config: 0
    },
    selectedStepId: {
      config: 0
    },
    busy: {
      config: 0
    }
  },
  fields: ["activePlanView"]
});
const __lwc_component_class_internal$3 = registerComponent(PlanWorkspace, {
  tmpl: _tmpl$3,
  sel: "c-plan-workspace",
  apiVersion: 66
});
function shortHash(value) {
  if (!value) return "";
  const text = String(value);
  return text.startsWith("sha256:") ? `sha256:${text.slice(7, 15)}` : text.slice(0, 12);
}
function containsRegistryDrift(value) {
  const text = String(value || "").toLowerCase();
  return text.includes("registry drift") || text.includes("registry validation failed") || text.includes("schema drift") || text.includes("tool schema") || text.includes("mcp registry validation");
}
function containsRedactionMarker(value) {
  if (value == null) return false;
  if (typeof value === "string") {
    return value.includes("***") || value.includes("[truncated]") || value.includes("_truncated");
  }
  if (Array.isArray(value)) {
    return value.some(item => containsRedactionMarker(item));
  }
  if (typeof value === "object") {
    return Object.entries(value).some(([key, item]) => key === "_truncated" || containsRedactionMarker(item));
  }
  return false;
}

function stylesheet$1(token, useActualHostSelector, useNativeDirPseudoclass) {
  var shadowSelector = token ? ("[" + token + "]") : "";
  return ".monitor-layout" + shadowSelector + " {display: grid;grid-template-columns: repeat(2, minmax(0, 1fr));gap: 0.75rem;align-items: start;}.monitor-list" + shadowSelector + ",.recommendation-list" + shadowSelector + " {display: grid;gap: 0.5rem;}.monitor-row" + shadowSelector + ",.recommendation" + shadowSelector + ",.last-run" + shadowSelector + ",.guardrail" + shadowSelector + " {display: grid;grid-template-columns: minmax(0, 1fr) auto auto;gap: 0.75rem;align-items: start;padding: 0.625rem;border: 1px solid #dddbda;border-radius: 0.25rem;background: #fff;}.recommendation" + shadowSelector + ",.last-run" + shadowSelector + ",.guardrail" + shadowSelector + " {grid-template-columns: minmax(0, 1fr) auto;}.monitor-row" + shadowSelector + " p" + shadowSelector + ",.recommendation" + shadowSelector + " p" + shadowSelector + ",.last-run" + shadowSelector + " p" + shadowSelector + ",.guardrail" + shadowSelector + " p" + shadowSelector + " {margin: 0.125rem 0 0;color: #706e6b;font-size: 0.75rem;}.status.active" + shadowSelector + ",.status.succeeded" + shadowSelector + ",.status.normal" + shadowSelector + ",.status.low" + shadowSelector + " {background: #eef8f1;color: #2e844a;}.status.attention-required" + shadowSelector + ",.status.pending-approval" + shadowSelector + ",.status.medium" + shadowSelector + ",.status.high" + shadowSelector + " {background: #fff7e0;color: #8a5a00;}.status.error" + shadowSelector + ",.status.critical" + shadowSelector + ",.status.rejected" + shadowSelector + " {background: #fff1f2;color: #ba0517;}.recommendations-card" + shadowSelector + " {grid-column: 1 / -1;}.recommendation-actions" + shadowSelector + " {grid-column: 1 / -1;display: flex;gap: 0.375rem;}.empty-state" + shadowSelector + " {min-height: 8rem;display: grid;place-items: center;color: #706e6b;border: 1px dashed #dddbda;border-radius: 0.25rem;background: #fafaf9;}@media (max-width: 900px) {.monitor-layout" + shadowSelector + " {grid-template-columns: 1fr;}}";
  /*LWC compiler v9.2.2*/
}
var _implicitStylesheets$1 = [stylesheet$1];

const $fragment1$2 = parseFragment`<div class="slds-card__header${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Goal Monitors</h2><p class="slds-text-body_small slds-text-color_weak${0}"${2}>Continuously watch the scenario after setup</p></div>`;
const $fragment2$2 = parseFragment`<article${"c0"}${2}><div${3}><strong${3}>${"t3"}</strong><p${3}>${"t5"}</p></div><span${"c6"}${2}>${"t7"}</span><button class="slds-button slds-button_neutral slds-button_x-small${0}"${"a8:data-monitor-id"}${2}>Run</button></article>`;
const $fragment3$2 = parseFragment`<div class="empty-state${0}"${2}>No monitors registered.</div>`;
const $fragment4$2 = parseFragment`<div class="slds-card__header${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Monitor Result</h2><p class="slds-text-body_small slds-text-color_weak${0}"${2}>Latest threshold evaluation</p></div>`;
const $fragment5$2 = parseFragment`<div class="last-run${0}"${2}><div${3}><strong${3}>${"t3"}</strong><p${3}>${"t5"}</p></div><span${"c6"}${2}>${"t7"}</span></div>`;
const $fragment6$2 = parseFragment`<div class="empty-state${0}"${2}>Run a monitor to view latest evaluation.</div>`;
const $fragment7$2 = parseFragment`<div class="slds-card__header${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Recommendations</h2><p class="slds-text-body_small slds-text-color_weak${0}"${2}>Agent suggestions created from monitor drift</p></div>`;
const $fragment8$1 = parseFragment`<div${3}><strong${3}>${"t2"}</strong><p${3}>${"t4"}</p><p${3}>${"t6"}</p></div>`;
const $fragment9$1 = parseFragment`<span${"c0"}${2}>${"t1"}</span>`;
const $fragment10$1 = parseFragment`<div class="recommendation-actions${0}"${2}><button class="slds-button slds-button_brand slds-button_x-small${0}"${"a1:data-recommendation-id"} data-transition="approve"${2}>Approve</button><button class="slds-button slds-button_neutral slds-button_x-small${0}"${"a3:data-recommendation-id"} data-transition="reject"${2}>Reject</button></div>`;
const $fragment11$1 = parseFragment`<div class="empty-state${0}"${2}>${"t1"}</div>`;
const $fragment12$1 = parseFragment`<div class="slds-card__header${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Activation Guardrails</h2><p class="slds-text-body_small slds-text-color_weak${0}"${2}>What the monitor keeps enforcing</p></div>`;
const $fragment13$1 = parseFragment`<strong${3}>${"t1"}</strong>`;
const $fragment14$1 = parseFragment`<p${3}>${"t1"}</p>`;
const $fragment15$1 = parseFragment`<p${3}>${"t1"}</p>`;
const stc0$2 = {
  classMap: {
    "monitor-layout": true
  },
  key: 0
};
const stc1$2 = {
  classMap: {
    "slds-card": true
  },
  key: 1
};
const stc2$2 = {
  classMap: {
    "slds-card__body": true,
    "slds-card__body_inner": true,
    "monitor-list": true
  },
  key: 4
};
const stc3$2 = [];
const stc4$2 = {
  classMap: {
    "slds-card": true
  },
  key: 9
};
const stc5$1 = {
  classMap: {
    "slds-card__body": true,
    "slds-card__body_inner": true
  },
  key: 12
};
const stc6$1 = {
  classMap: {
    "slds-card": true,
    "recommendations-card": true
  },
  key: 17
};
const stc7$1 = {
  classMap: {
    "slds-card__body": true,
    "slds-card__body_inner": true,
    "recommendation-list": true
  },
  key: 20
};
const stc8$1 = {
  classMap: {
    "slds-card": true
  },
  key: 30
};
const stc9$1 = {
  classMap: {
    "slds-card__body": true,
    "slds-card__body_inner": true,
    "guardrail": true
  },
  key: 33
};
function tmpl$2($api, $cmp, $slotset, $ctx) {
  const {st: api_static_fragment, ncls: api_normalize_class_name, k: api_key, d: api_dynamic_text, b: api_bind, sp: api_static_part, i: api_iterator, f: api_flatten, h: api_element} = $api;
  const {_m0, _m1, _m2} = $ctx;
  return [api_element("div", stc0$2, [api_element("section", stc1$2, [api_static_fragment($fragment1$2, 3), api_element("div", stc2$2, api_flatten([$cmp.hasMonitors ? api_iterator($cmp.monitorRows, function (monitor) {
    return api_static_fragment($fragment2$2, api_key(6, monitor.id), [api_static_part(0, {
      className: api_normalize_class_name(monitor.className)
    }, null), api_static_part(3, null, api_dynamic_text(monitor.metric)), api_static_part(5, null, api_dynamic_text(monitor.detail)), api_static_part(6, {
      className: api_normalize_class_name(monitor.statusClass)
    }, null), api_static_part(7, null, api_dynamic_text(monitor.status)), api_static_part(8, {
      on: _m0 || ($ctx._m0 = {
        "click": api_bind($cmp.handleRunMonitor)
      }),
      attrs: {
        "data-monitor-id": monitor.id
      }
    }, null)]);
  }) : stc3$2, !$cmp.hasMonitors ? api_static_fragment($fragment3$2, 8) : null]))]), api_element("section", stc4$2, [api_static_fragment($fragment4$2, 11), api_element("div", stc5$1, [$cmp.hasLastRun ? api_static_fragment($fragment5$2, 14, [api_static_part(3, null, api_dynamic_text($cmp.lastMonitorRun.recommendation)), api_static_part(5, null, api_dynamic_text($cmp.lastRunObserved)), api_static_part(6, {
    className: api_normalize_class_name($cmp.lastRunClass)
  }, null), api_static_part(7, null, api_dynamic_text($cmp.lastMonitorRun.status))]) : null, !$cmp.hasLastRun ? api_static_fragment($fragment6$2, 16) : null])]), api_element("section", stc6$1, [api_static_fragment($fragment7$2, 19), api_element("div", stc7$1, api_flatten([$cmp.hasRecommendations ? api_iterator($cmp.recommendationRows, function (item) {
    return api_element("article", {
      className: api_normalize_class_name(item.className),
      key: api_key(21, item.key)
    }, [api_static_fragment($fragment8$1, 23, [api_static_part(2, null, api_dynamic_text(item.title)), api_static_part(4, null, api_dynamic_text(item.detail)), api_static_part(6, null, api_dynamic_text(item.observed))]), api_static_fragment($fragment9$1, 25, [api_static_part(0, {
      className: api_normalize_class_name(item.badgeClass)
    }, null), api_static_part(1, null, api_dynamic_text(item.badge))]), item.canApprove ? api_static_fragment($fragment10$1, 27, [api_static_part(1, {
      on: _m1 || ($ctx._m1 = {
        "click": api_bind($cmp.handleRecommendation)
      }),
      attrs: {
        "data-recommendation-id": item.id
      }
    }, null), api_static_part(3, {
      on: _m2 || ($ctx._m2 = {
        "click": api_bind($cmp.handleRecommendation)
      }),
      attrs: {
        "data-recommendation-id": item.id
      }
    }, null)]) : null]);
  }) : stc3$2, !$cmp.hasRecommendations ? api_static_fragment($fragment11$1, 29, [api_static_part(1, null, api_dynamic_text($cmp.recommendationFallback))]) : null]))]), api_element("section", stc8$1, [api_static_fragment($fragment12$1, 32), api_element("div", stc9$1, [$cmp.activation ? api_static_fragment($fragment13$1, 35, [api_static_part(1, null, api_dynamic_text($cmp.activation.provider))]) : null, $cmp.activation ? api_static_fragment($fragment14$1, 37, [api_static_part(1, null, api_dynamic_text($cmp.activation.safetyRule))]) : null, $cmp.activation ? api_static_fragment($fragment15$1, 39, [api_static_part(1, null, api_dynamic_text($cmp.activation.outcomeJoin))]) : null])])])];
  /*LWC compiler v9.2.2*/
}
var _tmpl$2 = registerTemplate(tmpl$2);
tmpl$2.stylesheets = [];
tmpl$2.stylesheetToken = "lwc-21ng59cavvv";
tmpl$2.legacyStylesheetToken = "c-monitorWorkspace_monitorWorkspace";
if (_implicitStylesheets$1) {
  tmpl$2.stylesheets.push.apply(tmpl$2.stylesheets, _implicitStylesheets$1);
}
freezeTemplate(tmpl$2);

class MonitorWorkspace extends LightningElement {
  constructor(...args) {
    super(...args);
    this.demo = void 0;
    this.monitors = [];
    this.lastMonitorRun = void 0;
    this.recommendations = [];
    this.recommendationUnavailable = void 0;
  }
  get monitorRows() {
    return (this.monitors || []).map(monitor => ({
      ...monitor,
      className: `monitor-row ${slug(monitor.status)}`,
      statusClass: `slds-badge status ${slug(monitor.status)}`,
      detail: `${monitor.cadence} • ${monitor.lastRunAt ? dateTime(monitor.lastRunAt) : "Not run yet"}`
    }));
  }
  get hasMonitors() {
    return this.monitorRows.length > 0;
  }
  get recommendationRows() {
    return (this.recommendations || []).map(item => ({
      ...item,
      key: item.id || item.title || item.metric,
      title: item.title || item.metric || "Recommendation",
      detail: item.detail || item.recommendation || item.rationale || item.summary || "Review recommended action.",
      observed: `${number(item.observedValue || 0)} observed${item.createdAt ? ` • ${dateTime(item.createdAt)}` : ""}`,
      className: `recommendation ${slug(item.priority || item.status)}`,
      badgeClass: `slds-badge status ${slug(item.priority || item.status)}`,
      badge: item.priority || item.status || "Review",
      canApprove: slug(item.status) === "pending-approval"
    }));
  }
  get hasRecommendations() {
    return this.recommendationRows.length > 0;
  }
  get recommendationFallback() {
    return this.recommendationUnavailable ? `Recommendation API unavailable: ${this.recommendationUnavailable}` : "No recommendations returned.";
  }
  get hasLastRun() {
    return Boolean(this.lastMonitorRun);
  }
  get lastRunClass() {
    return `slds-badge status ${slug(this.lastMonitorRun?.status)}`;
  }
  get lastRunObserved() {
    return `${number(this.lastMonitorRun?.observedValue || 0)} observed`;
  }
  get activation() {
    return this.demo?.emailActivation;
  }
  handleRunMonitor(event) {
    this.dispatchEvent(new CustomEvent("runmonitor", {
      detail: {
        monitorId: event.currentTarget.dataset.monitorId
      }
    }));
  }
  handleRecommendation(event) {
    this.dispatchEvent(new CustomEvent("recommendationtransition", {
      detail: {
        recommendationId: event.currentTarget.dataset.recommendationId,
        transition: event.currentTarget.dataset.transition
      }
    }));
  }
  /*LWC compiler v9.2.2*/
}
registerDecorators(MonitorWorkspace, {
  publicProps: {
    demo: {
      config: 0
    },
    monitors: {
      config: 0
    },
    lastMonitorRun: {
      config: 0
    },
    recommendations: {
      config: 0
    },
    recommendationUnavailable: {
      config: 0
    }
  }
});
const __lwc_component_class_internal$2 = registerComponent(MonitorWorkspace, {
  tmpl: _tmpl$2,
  sel: "c-monitor-workspace",
  apiVersion: 66
});

function stylesheet(token, useActualHostSelector, useNativeDirPseudoclass) {
  var shadowSelector = token ? ("[" + token + "]") : "";
  return ".audit-layout" + shadowSelector + " {display: grid;grid-template-columns: repeat(2, minmax(0, 1fr));gap: 0.75rem;align-items: start;}.audit-list" + shadowSelector + " {display: grid;gap: 0.5rem;}.audit-row" + shadowSelector + " {padding: 0.625rem;border: 1px solid #dddbda;border-radius: 0.25rem;background: #fff;}.audit-row" + shadowSelector + " p" + shadowSelector + " {margin-top: 0.125rem;color: #706e6b;font-size: 0.75rem;}.json-card" + shadowSelector + " {min-width: 0;}.json-card" + shadowSelector + " pre" + shadowSelector + " {max-height: 28rem;overflow: auto;color: #032d60;font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;font-size: 0.75rem;line-height: 1.45;white-space: pre-wrap;}@media (max-width: 900px) {.audit-layout" + shadowSelector + " {grid-template-columns: 1fr;}}";
  /*LWC compiler v9.2.2*/
}
var _implicitStylesheets = [stylesheet];

const $fragment1$1 = parseFragment`<div class="slds-card__header${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Trusted Context</h2><p class="slds-text-body_small slds-text-color_weak${0}"${2}>Signal sources used by the scenario</p></div>`;
const $fragment2$1 = parseFragment`<article class="audit-row${0}"${2}><strong${3}>${"t2"}</strong><p${3}>${"t4"}</p></article>`;
const $fragment3$1 = parseFragment`<div class="slds-card__header${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Run Plan</h2><p class="slds-text-body_small slds-text-color_weak${0}"${2}>Operator narrative for the workflow</p></div>`;
const $fragment4$1 = parseFragment`<article class="audit-row${0}"${2}><strong${3}>${"t2"}</strong><p${3}>${"t4"}</p></article>`;
const $fragment5$1 = parseFragment`<section class="slds-card json-card${0}"${2}><div class="slds-card__header${0}"${2}><h2 class="slds-card__header-title${0}"${2}>PlanSpec JSON</h2></div><div class="slds-card__body slds-card__body_inner${0}"${2}><pre${3}>${"t6"}</pre></div></section>`;
const $fragment6$1 = parseFragment`<section class="slds-card json-card${0}"${2}><div class="slds-card__header${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Run State JSON</h2></div><div class="slds-card__body slds-card__body_inner${0}"${2}><pre${3}>${"t6"}</pre></div></section>`;
const $fragment7$1 = parseFragment`<section class="slds-card json-card${0}"${2}><div class="slds-card__header${0}"${2}><h2 class="slds-card__header-title${0}"${2}>Data 360 Client</h2></div><div class="slds-card__body slds-card__body_inner${0}"${2}><pre${3}>${"t6"}</pre></div></section>`;
const stc0$1 = {
  classMap: {
    "audit-layout": true
  },
  key: 0
};
const stc1$1 = {
  classMap: {
    "slds-card": true
  },
  key: 1
};
const stc2$1 = {
  classMap: {
    "slds-card__body": true,
    "slds-card__body_inner": true,
    "audit-list": true
  },
  key: 4
};
const stc3$1 = {
  classMap: {
    "slds-card": true
  },
  key: 7
};
const stc4$1 = {
  classMap: {
    "slds-card__body": true,
    "slds-card__body_inner": true,
    "audit-list": true
  },
  key: 10
};
function tmpl$1($api, $cmp, $slotset, $ctx) {
  const {st: api_static_fragment, k: api_key, d: api_dynamic_text, sp: api_static_part, i: api_iterator, h: api_element} = $api;
  return [api_element("div", stc0$1, [api_element("section", stc1$1, [api_static_fragment($fragment1$1, 3), api_element("div", stc2$1, api_iterator($cmp.signalSources, function (source) {
    return api_static_fragment($fragment2$1, api_key(6, source.name), [api_static_part(2, null, api_dynamic_text(source.name)), api_static_part(4, null, api_dynamic_text(source.detail))]);
  }))]), api_element("section", stc3$1, [api_static_fragment($fragment3$1, 9), api_element("div", stc4$1, api_iterator($cmp.planStages, function (stage) {
    return api_static_fragment($fragment4$1, api_key(12, stage.title), [api_static_part(2, null, api_dynamic_text(stage.title)), api_static_part(4, null, api_dynamic_text(stage.detail))]);
  }))]), api_static_fragment($fragment5$1, 14, [api_static_part(6, null, api_dynamic_text($cmp.planJson))]), api_static_fragment($fragment6$1, 16, [api_static_part(6, null, api_dynamic_text($cmp.runJson))]), api_static_fragment($fragment7$1, 18, [api_static_part(6, null, api_dynamic_text($cmp.diagnosticsJson))])])];
  /*LWC compiler v9.2.2*/
}
var _tmpl$1 = registerTemplate(tmpl$1);
tmpl$1.stylesheets = [];
tmpl$1.stylesheetToken = "lwc-6fmttpvmcer";
tmpl$1.legacyStylesheetToken = "c-auditWorkspace_auditWorkspace";
if (_implicitStylesheets) {
  tmpl$1.stylesheets.push.apply(tmpl$1.stylesheets, _implicitStylesheets);
}
freezeTemplate(tmpl$1);

class AuditWorkspace extends LightningElement {
  constructor(...args) {
    super(...args);
    this.demo = void 0;
    this.currentDraft = void 0;
    this.currentRun = void 0;
    this.diagnostics = void 0;
  }
  get signalSources() {
    return this.demo?.signalSources || [];
  }
  get planStages() {
    return this.demo?.plan || [];
  }
  get planJson() {
    return toJson(this.currentDraft?.plan || {});
  }
  get runJson() {
    return toJson(this.currentRun || {});
  }
  get diagnosticsJson() {
    return toJson(this.diagnostics || {});
  }
  /*LWC compiler v9.2.2*/
}
registerDecorators(AuditWorkspace, {
  publicProps: {
    demo: {
      config: 0
    },
    currentDraft: {
      config: 0
    },
    currentRun: {
      config: 0
    },
    diagnostics: {
      config: 0
    }
  }
});
const __lwc_component_class_internal$1 = registerComponent(AuditWorkspace, {
  tmpl: _tmpl$1,
  sel: "c-audit-workspace",
  apiVersion: 66
});

const $fragment1 = parseFragment`<div class="auth-brand${0}"${2}><span class="brand-mark${0}"${2}>D360</span><div${3}><h1${3}>Data 360 Agent Console</h1><p${3}>${"t7"}</p></div></div>`;
const $fragment2 = parseFragment`<button class="slds-button slds-button_brand${0}"${"a0:disabled"}${2}>Create organization</button>`;
const $fragment3 = parseFragment`<button class="slds-button slds-button_brand${0}"${"a0:disabled"}${2}>Log in</button>`;
const $fragment4 = parseFragment`<div class="slds-notify slds-notify_alert slds-alert_error${0}" role="alert"${2}>${"t1"}</div>`;
const $fragment5 = parseFragment`<div class="brand${0}"${2}><span class="brand-mark${0}"${2}>D360</span><strong${3}>Agent Console</strong></div>`;
const $fragment6 = parseFragment`<a${"c0"}${"a0:href"}${"a0:data-tab"}${2}>${"t1"}</a>`;
const $fragment7 = parseFragment`<div class="side-footer${0}"${2}><span class="footer-user${0}"${2}>${"t2"}</span><span class="footer-model${0}"${2}>${"t4"}</span><button class="slds-button slds-button_neutral${0}"${2}>Log out</button></div>`;
const $fragment8 = parseFragment`<div class="settings-title${0}"${2}>Settings</div>`;
const $fragment9 = parseFragment`<button${"c0"}${"a0:data-section"}${2}>${"t1"}</button>`;
const $fragment10 = parseFragment`<header class="settings-header${0}"${2}><div${3}><p${3}>MCP servers</p><h1${3}>Connect to custom MCP servers</h1></div><button class="slds-button slds-button_brand${0}"${"a6:disabled"}${2}>Save and validate</button></header>`;
const $fragment11 = parseFragment`<div class="settings-feedback passed${0}" role="status"${2}>${"t1"}</div>`;
const $fragment12 = parseFragment`<strong${3}>${"t1"}</strong>`;
const $fragment13 = parseFragment`<span${3}>${"t1"}</span>`;
const $fragment14 = parseFragment`<small${3}>${"t1"}</small>`;
const $fragment15 = parseFragment`<code${3}>${"t1"}</code>`;
const $fragment16 = parseFragment`<button${"c0"}${"a0:data-server-id"}${2}><span${3}>${"t2"}</span><small${3}>${"t4"}</small></button>`;
const $fragment17 = parseFragment`<span${3}>Enabled</span>`;
const $fragment18 = parseFragment`<div class="field-group${0}"${2}><span class="field-label${0}"${2}>Transport</span><div class="transport-picker${0}" role="group" aria-label="Transport"${2}><button type="button"${"c4"}${"a4:data-server-id"} data-transport="stdio"${2}>STDIO</button><button type="button"${"c6"}${"a6:data-server-id"} data-transport="streamable-http"${2}>Streamable HTTP</button></div></div>`;
const $fragment19 = parseFragment`<span class="field-label${0}"${2}>Arguments</span>`;
const $fragment20 = parseFragment`<button type="button" class="icon-button${0}" title="Remove argument"${"a0:data-server-id"}${"a0:data-item-id"}${2}>×</button>`;
const $fragment21 = parseFragment`<button type="button" class="text-action${0}"${"a0:data-server-id"}${2}>+ Add argument</button>`;
const $fragment22 = parseFragment`<span class="field-label${0}"${2}>Environment variables</span>`;
const $fragment23 = parseFragment`<button type="button" class="icon-button${0}" title="Remove environment variable"${"a0:data-server-id"}${"a0:data-item-id"}${2}>×</button>`;
const $fragment24 = parseFragment`<button type="button" class="text-action${0}"${"a0:data-server-id"}${2}>+ Add environment variable</button>`;
const $fragment25 = parseFragment`<span class="field-label${0}"${2}>Environment variable passthrough</span>`;
const $fragment26 = parseFragment`<button type="button" class="icon-button${0}" title="Remove passthrough variable"${"a0:data-server-id"}${"a0:data-item-id"}${2}>×</button>`;
const $fragment27 = parseFragment`<button type="button" class="text-action${0}"${"a0:data-server-id"}${2}>+ Add variable</button>`;
const $fragment28 = parseFragment`<h1${3}>Model</h1>`;
const $fragment29 = parseFragment`<label${3}>Provider<select class="slds-select${0}" data-field="settingsProvider"${2}><option value="anthropic"${"a3:selected"}${3}>Anthropic</option><option value="openrouter"${"a5:selected"}${3}>OpenRouter</option></select></label>`;
const $fragment30 = parseFragment`<option${"a0:value"}${"a0:selected"}${3}>${"t1"}</option>`;
const $fragment31 = parseFragment`<button class="slds-button slds-button_brand${0}"${"a0:disabled"}${2}>Save model settings</button>`;
const $fragment32 = parseFragment`<h1${3}>Organizations</h1>`;
const $fragment33 = parseFragment`<button class="slds-button slds-button_neutral${0}"${"a0:disabled"}${2}>Create</button>`;
const $fragment34 = parseFragment`<button${"c0"}${"a0:data-organization-id"}${2}>${"t1"}</button>`;
const $fragment35 = parseFragment`<h1${3}>Users</h1>`;
const $fragment36 = parseFragment`<select class="slds-select${0}" data-field="newUserRole"${2}><option value="MEMBER"${"a1:selected"}${3}>Member</option><option value="ADMIN"${"a3:selected"}${3}>Admin</option><option value="VIEWER"${"a5:selected"}${3}>Viewer</option></select>`;
const $fragment37 = parseFragment`<button class="slds-button slds-button_neutral${0}"${"a0:disabled"}${2}>Create user</button>`;
const $fragment38 = parseFragment`<div class="user-row${0}"${2}><strong${3}>${"t2"}</strong><span${3}>${"t4"}</span></div>`;
const $fragment39 = parseFragment`<div class="slds-notify slds-notify_alert slds-alert_error floating-alert${0}" role="alert"${2}>${"t1"}</div>`;
const $fragment40 = parseFragment`<div class="slds-notify slds-notify_alert export-toast${0}" role="status"${2}><span${3}>${"t2"}</span><button class="toast-dismiss${0}" type="button" title="Dismiss export notification" aria-label="Dismiss export notification"${2}>×</button></div>`;
const stc0 = {
  classMap: {
    "app-frame": true
  },
  key: 0
};
const stc1 = {
  classMap: {
    "auth-screen": true
  },
  key: 1
};
const stc2 = {
  classMap: {
    "auth-panel": true
  },
  key: 2
};
const stc3 = {
  classMap: {
    "auth-form": true
  },
  key: 5
};
const stc4 = {
  key: 6
};
const stc5 = {
  "slds-input": true
};
const stc6 = {
  "data-field": "setupOrganizationName"
};
const stc7 = {
  key: 8
};
const stc8 = {
  "data-field": "setupDisplayName"
};
const stc9 = {
  key: 10
};
const stc10 = {
  "type": "email",
  "data-field": "setupEmail"
};
const stc11 = {
  key: 12
};
const stc12 = {
  "type": "password",
  "data-field": "setupPassword"
};
const stc13 = {
  classMap: {
    "auth-form": true
  },
  key: 16
};
const stc14 = {
  key: 17
};
const stc15 = {
  "type": "email",
  "data-field": "loginEmail"
};
const stc16 = {
  key: 19
};
const stc17 = {
  "type": "password",
  "data-field": "loginPassword"
};
const stc18 = {
  classMap: {
    "side-nav": true
  },
  key: 25
};
const stc19 = {
  attrs: {
    "aria-label": "Workspace"
  },
  key: 28
};
const stc20 = {
  classMap: {
    "workspace": true
  },
  key: 33
};
const stc21 = {
  classMap: {
    "settings-shell": true
  },
  attrs: {
    "aria-label": "Admin settings"
  },
  key: 39
};
const stc22 = {
  classMap: {
    "settings-sidebar": true
  },
  key: 40
};
const stc23 = {
  classMap: {
    "settings-main": true
  },
  key: 45
};
const stc24 = {
  "aria-label": "MCP validation result"
};
const stc25 = {
  classMap: {
    "validation-list": true
  },
  key: 53
};
const stc26 = {
  classMap: {
    "mcp-settings-layout": true
  },
  key: 61
};
const stc27 = {
  classMap: {
    "mcp-server-list": true
  },
  attrs: {
    "aria-label": "MCP server list"
  },
  key: 62
};
const stc28 = {
  classMap: {
    "mcp-editor": true
  },
  key: 65
};
const stc29 = {
  key: 66
};
const stc30 = {
  classMap: {
    "toggle-label": true
  },
  key: 68
};
const stc31 = {
  key: 74
};
const stc32 = {
  "slds-input": true,
  "monospace-input": true
};
const stc33 = {
  classMap: {
    "field-group": true
  },
  key: 76
};
const stc34 = {
  "repeat-row": true
};
const stc35 = {
  classMap: {
    "field-group": true
  },
  key: 85
};
const stc36 = {
  "repeat-row": true,
  "two-col": true
};
const stc37 = {
  classMap: {
    "field-group": true
  },
  key: 95
};
const stc38 = {
  key: 104
};
const stc39 = {
  key: 106
};
const stc40 = {
  classMap: {
    "settings-card": true
  },
  key: 108
};
const stc41 = {
  key: 113
};
const stc42 = {
  "slds-select": true
};
const stc43 = {
  "data-field": "settingsModel"
};
const stc44 = {
  key: 118
};
const stc45 = {
  classMap: {
    "settings-card": true
  },
  key: 122
};
const stc46 = {
  classMap: {
    "inline-form": true
  },
  key: 125
};
const stc47 = {
  "data-field": "newOrganizationName",
  "placeholder": "Organization name"
};
const stc48 = {
  classMap: {
    "settings-card": true
  },
  key: 131
};
const stc49 = {
  classMap: {
    "user-form": true
  },
  key: 134
};
const stc50 = {
  "data-field": "newUserName",
  "placeholder": "Name"
};
const stc51 = {
  "type": "email",
  "data-field": "newUserEmail",
  "placeholder": "Email"
};
const stc52 = {
  "type": "password",
  "data-field": "newUserPassword",
  "placeholder": "Temporary password"
};
function tmpl($api, $cmp, $slotset, $ctx) {
  const {d: api_dynamic_text, sp: api_static_part, st: api_static_fragment, t: api_text, b: api_bind, h: api_element, ncls: api_normalize_class_name, k: api_key, i: api_iterator, c: api_custom_element, f: api_flatten} = $api;
  const {_m0, _m1, _m2, _m3, _m4, _m5, _m6, _m7, _m8, _m9, _m10, _m11, _m12, _m13, _m14, _m15, _m16, _m17, _m18, _m19, _m20, _m21, _m22, _m23, _m24, _m25, _m26, _m27, _m28, _m29, _m30, _m31, _m32, _m33, _m34, _m35, _m36, _m37, _m38, _m39, _m40, _m41, _m42, _m43, _m44, _m45, _m46, _m47, _m48, _m49, _m50, _m51, _m52, _m53, _m54, _m55, _m56, _m57, _m58, _m59, _m60, _m61, _m62, _m63} = $ctx;
  return [api_element("div", stc0, [$cmp.showAuthScreen ? api_element("main", stc1, [api_element("section", stc2, [api_static_fragment($fragment1, 4, [api_static_part(7, null, api_dynamic_text($cmp.authSubtitle))]), $cmp.setupRequired ? api_element("div", stc3, [api_element("label", stc4, [api_text("Organization"), api_element("input", {
    classMap: stc5,
    attrs: stc6,
    props: {
      "value": $cmp.setupOrganizationName
    },
    key: 7,
    on: _m0 || ($ctx._m0 = {
      "input": api_bind($cmp.handleFieldChange)
    })
  })]), api_element("label", stc7, [api_text("Name"), api_element("input", {
    classMap: stc5,
    attrs: stc8,
    props: {
      "value": $cmp.setupDisplayName
    },
    key: 9,
    on: _m1 || ($ctx._m1 = {
      "input": api_bind($cmp.handleFieldChange)
    })
  })]), api_element("label", stc9, [api_text("Email"), api_element("input", {
    classMap: stc5,
    attrs: stc10,
    props: {
      "value": $cmp.setupEmail
    },
    key: 11,
    on: _m2 || ($ctx._m2 = {
      "input": api_bind($cmp.handleFieldChange)
    })
  })]), api_element("label", stc11, [api_text("Password"), api_element("input", {
    classMap: stc5,
    attrs: stc12,
    props: {
      "value": $cmp.setupPassword
    },
    key: 13,
    on: _m3 || ($ctx._m3 = {
      "input": api_bind($cmp.handleFieldChange)
    })
  })]), api_static_fragment($fragment2, 15, [api_static_part(0, {
    on: _m5 || ($ctx._m5 = {
      "click": api_bind($cmp.handleBootstrap)
    }),
    attrs: {
      "disabled": $cmp.busy.auth ? "" : null
    }
  }, null)])]) : null, !$cmp.setupRequired ? api_element("div", stc13, [api_element("label", stc14, [api_text("Email"), api_element("input", {
    classMap: stc5,
    attrs: stc15,
    props: {
      "value": $cmp.loginEmail
    },
    key: 18,
    on: _m6 || ($ctx._m6 = {
      "input": api_bind($cmp.handleFieldChange)
    })
  })]), api_element("label", stc16, [api_text("Password"), api_element("input", {
    classMap: stc5,
    attrs: stc17,
    props: {
      "value": $cmp.loginPassword
    },
    key: 20,
    on: _m7 || ($ctx._m7 = {
      "input": api_bind($cmp.handleFieldChange),
      "keydown": api_bind($cmp.handleLoginKeydown)
    })
  })]), api_static_fragment($fragment3, 22, [api_static_part(0, {
    on: _m9 || ($ctx._m9 = {
      "click": api_bind($cmp.handleLogin)
    }),
    attrs: {
      "disabled": $cmp.busy.auth ? "" : null
    }
  }, null)])]) : null, $cmp.error ? api_static_fragment($fragment4, 24, [api_static_part(1, null, api_dynamic_text($cmp.error))]) : null])]) : null, !$cmp.showAuthScreen ? api_element("aside", stc18, [api_static_fragment($fragment5, 27), api_element("nav", stc19, api_iterator($cmp.tabs, function (tab) {
    return api_static_fragment($fragment6, api_key(30, tab.id), [api_static_part(0, {
      on: _m11 || ($ctx._m11 = {
        "click": api_bind($cmp.handleTab)
      }),
      className: api_normalize_class_name(tab.className),
      attrs: {
        "href": tab.href,
        "data-tab": tab.id
      }
    }, null), api_static_part(1, null, api_dynamic_text(tab.label))]);
  })), api_static_fragment($fragment7, 32, [api_static_part(2, null, api_dynamic_text($cmp.userLabel)), api_static_part(4, null, api_dynamic_text($cmp.compactModelLabel)), api_static_part(5, {
    on: _m12 || ($ctx._m12 = {
      "click": api_bind($cmp.handleLogout)
    })
  }, null)])]) : null, !$cmp.showAuthScreen ? api_element("main", stc20, [$cmp.isChat ? api_custom_element("c-chat-workspace", __lwc_component_class_internal$5, {
    props: {
      "messages": $cmp.messages,
      "plannerStatus": $cmp.plannerStatus,
      "modelLabel": $cmp.modelLabel,
      "userLabel": $cmp.userLabel,
      "currentDraft": $cmp.currentDraft,
      "approvedPlan": $cmp.currentApprovedPlan,
      "currentRun": $cmp.currentRun,
      "busy": $cmp.busy,
      "chatMode": $cmp.chatMode
    },
    key: 34,
    on: _m13 || ($ctx._m13 = {
      "sendmessage": api_bind($cmp.handleChatMessage),
      "chatmodechange": api_bind($cmp.handleChatModeChange),
      "approveplan": api_bind($cmp.handleApprovePlan),
      "startplan": api_bind($cmp.handleStartPlan),
      "exportplanspec": api_bind($cmp.handleExportPlanSpec)
    })
  }) : null, $cmp.isTemplates ? api_custom_element("c-template-workspace", __lwc_component_class_internal$4, {
    props: {
      "scenarios": $cmp.scenarios,
      "templates": $cmp.templates,
      "selectedScenarioId": $cmp.selectedScenarioId,
      "currentDraft": $cmp.currentDraft,
      "currentRun": $cmp.currentRun,
      "busy": $cmp.busy
    },
    key: 35,
    on: _m14 || ($ctx._m14 = {
      "scenariochange": api_bind($cmp.handleScenarioChange),
      "usetemplate": api_bind($cmp.handleUseTemplate),
      "instantiatetemplate": api_bind($cmp.handleInstantiateTemplate)
    })
  }) : null, $cmp.isReview ? api_custom_element("c-plan-workspace", __lwc_component_class_internal$3, {
    props: {
      "scenarios": $cmp.scenarios,
      "selectedScenarioId": $cmp.selectedScenarioId,
      "goal": $cmp.goal,
      "diagnostics": $cmp.diagnostics,
      "plannerStatus": $cmp.plannerStatus,
      "currentDraft": $cmp.currentDraft,
      "approvedPlan": $cmp.currentApprovedPlan,
      "currentRun": $cmp.currentRun,
      "approvalHistory": $cmp.approvalHistory,
      "approvalHistoryUnavailable": $cmp.approvalHistoryUnavailable,
      "selectedStepId": $cmp.selectedStepId,
      "busy": $cmp.busy
    },
    key: 36,
    on: _m15 || ($ctx._m15 = {
      "scenariochange": api_bind($cmp.handleScenarioChange),
      "goalchange": api_bind($cmp.handleGoalChange),
      "draftplan": api_bind($cmp.handleDraftPlan),
      "approveplan": api_bind($cmp.handleApprovePlan),
      "startplan": api_bind($cmp.handleStartPlan),
      "exportplanspec": api_bind($cmp.handleExportPlanSpec),
      "smokedata360": api_bind($cmp.handleSmokeData360),
      "selectstep": api_bind($cmp.handleSelectStep),
      "approvestep": api_bind($cmp.handleApproveStep)
    })
  }) : null, $cmp.isMonitors ? api_custom_element("c-monitor-workspace", __lwc_component_class_internal$2, {
    props: {
      "demo": $cmp.demo,
      "monitors": $cmp.monitors,
      "lastMonitorRun": $cmp.lastMonitorRun,
      "recommendations": $cmp.recommendations,
      "recommendationUnavailable": $cmp.recommendationUnavailable
    },
    key: 37,
    on: _m16 || ($ctx._m16 = {
      "runmonitor": api_bind($cmp.handleRunMonitor),
      "recommendationtransition": api_bind($cmp.handleRecommendationTransition)
    })
  }) : null, $cmp.isAudit ? api_custom_element("c-audit-workspace", __lwc_component_class_internal$1, {
    props: {
      "demo": $cmp.demo,
      "currentDraft": $cmp.currentDraft,
      "currentRun": $cmp.currentRun,
      "diagnostics": $cmp.diagnostics
    },
    key: 38
  }) : null, $cmp.isAdmin ? api_element("section", stc21, [api_element("aside", stc22, api_flatten([api_static_fragment($fragment8, 42), api_iterator($cmp.adminSections, function (section) {
    return api_static_fragment($fragment9, api_key(44, section.id), [api_static_part(0, {
      on: _m18 || ($ctx._m18 = {
        "click": api_bind($cmp.handleAdminSection)
      }),
      className: api_normalize_class_name(section.className),
      attrs: {
        "data-section": section.id
      }
    }, null), api_static_part(1, null, api_dynamic_text(section.label))]);
  })])), api_element("section", stc23, [$cmp.isAdminMcp ? api_static_fragment($fragment10, 47, [api_static_part(6, {
    on: _m19 || ($ctx._m19 = {
      "click": api_bind($cmp.handleSaveMcpSettings)
    }),
    attrs: {
      "disabled": $cmp.busy.saveMcpSettings ? "" : null
    }
  }, null)]) : null, $cmp.isAdminMcp ? $cmp.mcpSaveMessage ? api_static_fragment($fragment11, 49, [api_static_part(1, null, api_dynamic_text($cmp.mcpSaveMessage))]) : null : null, $cmp.isAdminMcp ? $cmp.mcpValidation ? api_element("section", {
    className: api_normalize_class_name($cmp.mcpValidationClass),
    attrs: stc24,
    key: 50
  }, [api_static_fragment($fragment12, 52, [api_static_part(1, null, api_dynamic_text($cmp.mcpValidation.message))]), api_element("div", stc25, api_iterator($cmp.mcpValidationRows, function (server) {
    return api_element("div", {
      className: api_normalize_class_name(server.className),
      key: api_key(54, server.id)
    }, [api_static_fragment($fragment13, 56, [api_static_part(1, null, api_dynamic_text(server.name))]), api_static_fragment($fragment14, 58, [api_static_part(1, null, api_dynamic_text(server.message))]), server.toolSummary ? api_static_fragment($fragment15, 60, [api_static_part(1, null, api_dynamic_text(server.toolSummary))]) : null]);
  }))]) : null : null, $cmp.isAdminMcp ? api_element("div", stc26, [api_element("nav", stc27, api_iterator($cmp.selectedMcpServerRows, function (server) {
    return api_static_fragment($fragment16, api_key(64, server.id), [api_static_part(0, {
      on: _m21 || ($ctx._m21 = {
        "click": api_bind($cmp.handleSelectMcpServer)
      }),
      className: api_normalize_class_name(server.className),
      attrs: {
        "data-server-id": server.id
      }
    }, null), api_static_part(2, null, api_dynamic_text(server.name)), api_static_part(4, null, api_dynamic_text(server.statusLabel))]);
  })), $cmp.selectedMcpServer ? api_element("form", stc28, [api_element("label", stc29, [api_text("Name"), api_element("input", {
    classMap: stc5,
    attrs: {
      "data-server-id": $cmp.selectedMcpServer.id,
      "data-field": "name"
    },
    props: {
      "value": $cmp.selectedMcpServer.name
    },
    key: 67,
    on: _m22 || ($ctx._m22 = {
      "input": api_bind($cmp.handleMcpFieldChange)
    })
  })]), api_element("label", stc30, [api_element("input", {
    attrs: {
      "type": "checkbox",
      "data-server-id": $cmp.selectedMcpServer.id
    },
    props: {
      "checked": $cmp.selectedMcpServer.enabled
    },
    key: 69,
    on: _m23 || ($ctx._m23 = {
      "change": api_bind($cmp.handleMcpEnabledChange)
    })
  }), api_static_fragment($fragment17, 71)]), api_static_fragment($fragment18, 73, [api_static_part(4, {
    on: _m24 || ($ctx._m24 = {
      "click": api_bind($cmp.handleMcpTransportSelect)
    }),
    className: api_normalize_class_name($cmp.mcpStdioButtonClass),
    attrs: {
      "data-server-id": $cmp.selectedMcpServer.id
    }
  }, null), api_static_part(6, {
    on: _m25 || ($ctx._m25 = {
      "click": api_bind($cmp.handleMcpTransportSelect)
    }),
    className: api_normalize_class_name($cmp.mcpHttpButtonClass),
    attrs: {
      "data-server-id": $cmp.selectedMcpServer.id
    }
  }, null)]), $cmp.selectedMcpIsStdio ? api_element("label", stc31, [api_text("Command to launch"), api_element("input", {
    classMap: stc32,
    attrs: {
      "data-server-id": $cmp.selectedMcpServer.id,
      "data-field": "command",
      "placeholder": "npx -y @salesforce/data360-mcp-server"
    },
    props: {
      "value": $cmp.selectedMcpServer.command
    },
    key: 75,
    on: _m26 || ($ctx._m26 = {
      "input": api_bind($cmp.handleMcpFieldChange)
    })
  })]) : null, $cmp.selectedMcpIsStdio ? api_element("div", stc33, api_flatten([api_static_fragment($fragment19, 78), api_iterator($cmp.selectedMcpServer.arguments, function (argument) {
    return api_element("div", {
      classMap: stc34,
      key: api_key(79, argument.id)
    }, [api_element("input", {
      classMap: stc32,
      attrs: {
        "data-server-id": $cmp.selectedMcpServer.id,
        "data-item-id": argument.id,
        "placeholder": "--flag=value"
      },
      props: {
        "value": argument.value
      },
      key: 80,
      on: _m27 || ($ctx._m27 = {
        "input": api_bind($cmp.handleMcpArgumentChange)
      })
    }), api_static_fragment($fragment20, 82, [api_static_part(0, {
      on: _m29 || ($ctx._m29 = {
        "click": api_bind($cmp.handleRemoveMcpArgument)
      }),
      attrs: {
        "data-server-id": $cmp.selectedMcpServer.id,
        "data-item-id": argument.id
      }
    }, null)])]);
  }), api_static_fragment($fragment21, 84, [api_static_part(0, {
    on: _m31 || ($ctx._m31 = {
      "click": api_bind($cmp.handleAddMcpArgument)
    }),
    attrs: {
      "data-server-id": $cmp.selectedMcpServer.id
    }
  }, null)])])) : null, $cmp.selectedMcpIsStdio ? api_element("div", stc35, api_flatten([api_static_fragment($fragment22, 87), api_iterator($cmp.selectedMcpServer.environment, function (variable) {
    return api_element("div", {
      classMap: stc36,
      key: api_key(88, variable.id)
    }, [api_element("input", {
      classMap: stc32,
      attrs: {
        "data-server-id": $cmp.selectedMcpServer.id,
        "data-item-id": variable.id,
        "data-field": "key",
        "placeholder": "KEY"
      },
      props: {
        "value": variable.key
      },
      key: 89,
      on: _m32 || ($ctx._m32 = {
        "input": api_bind($cmp.handleMcpEnvironmentChange)
      })
    }), api_element("input", {
      classMap: stc32,
      attrs: {
        "data-server-id": $cmp.selectedMcpServer.id,
        "data-item-id": variable.id,
        "data-field": "value",
        "placeholder": "value"
      },
      props: {
        "value": variable.value
      },
      key: 90,
      on: _m33 || ($ctx._m33 = {
        "input": api_bind($cmp.handleMcpEnvironmentChange)
      })
    }), api_static_fragment($fragment23, 92, [api_static_part(0, {
      on: _m35 || ($ctx._m35 = {
        "click": api_bind($cmp.handleRemoveMcpEnvironment)
      }),
      attrs: {
        "data-server-id": $cmp.selectedMcpServer.id,
        "data-item-id": variable.id
      }
    }, null)])]);
  }), api_static_fragment($fragment24, 94, [api_static_part(0, {
    on: _m37 || ($ctx._m37 = {
      "click": api_bind($cmp.handleAddMcpEnvironment)
    }),
    attrs: {
      "data-server-id": $cmp.selectedMcpServer.id
    }
  }, null)])])) : null, $cmp.selectedMcpIsStdio ? api_element("div", stc37, api_flatten([api_static_fragment($fragment25, 97), api_iterator($cmp.selectedMcpServer.environmentPassthrough, function (variable) {
    return api_element("div", {
      classMap: stc34,
      key: api_key(98, variable.id)
    }, [api_element("input", {
      classMap: stc32,
      attrs: {
        "data-server-id": $cmp.selectedMcpServer.id,
        "data-item-id": variable.id,
        "placeholder": "SALESFORCE_ACCESS_TOKEN"
      },
      props: {
        "value": variable.value
      },
      key: 99,
      on: _m38 || ($ctx._m38 = {
        "input": api_bind($cmp.handleMcpPassthroughChange)
      })
    }), api_static_fragment($fragment26, 101, [api_static_part(0, {
      on: _m40 || ($ctx._m40 = {
        "click": api_bind($cmp.handleRemoveMcpPassthrough)
      }),
      attrs: {
        "data-server-id": $cmp.selectedMcpServer.id,
        "data-item-id": variable.id
      }
    }, null)])]);
  }), api_static_fragment($fragment27, 103, [api_static_part(0, {
    on: _m42 || ($ctx._m42 = {
      "click": api_bind($cmp.handleAddMcpPassthrough)
    }),
    attrs: {
      "data-server-id": $cmp.selectedMcpServer.id
    }
  }, null)])])) : null, $cmp.selectedMcpIsStdio ? api_element("label", stc38, [api_text("Working directory"), api_element("input", {
    classMap: stc32,
    attrs: {
      "data-server-id": $cmp.selectedMcpServer.id,
      "data-field": "workingDirectory",
      "placeholder": "/Users/me/project"
    },
    props: {
      "value": $cmp.selectedMcpServer.workingDirectory
    },
    key: 105,
    on: _m43 || ($ctx._m43 = {
      "input": api_bind($cmp.handleMcpFieldChange)
    })
  })]) : null, $cmp.selectedMcpIsHttp ? api_element("label", stc39, [api_text("Endpoint"), api_element("input", {
    classMap: stc32,
    attrs: {
      "data-server-id": $cmp.selectedMcpServer.id,
      "data-field": "endpoint",
      "placeholder": "https://example.com/mcp"
    },
    props: {
      "value": $cmp.selectedMcpServer.endpoint
    },
    key: 107,
    on: _m44 || ($ctx._m44 = {
      "input": api_bind($cmp.handleMcpFieldChange)
    })
  })]) : null]) : null]) : null, $cmp.isAdminModel ? api_element("section", stc40, [api_static_fragment($fragment28, 110), api_static_fragment($fragment29, 112, [api_static_part(2, {
    on: _m45 || ($ctx._m45 = {
      "change": api_bind($cmp.handleFieldChange)
    })
  }, null), api_static_part(3, {
    attrs: {
      "selected": $cmp.settingsProviderIsAnthropic ? "" : null
    }
  }, null), api_static_part(5, {
    attrs: {
      "selected": $cmp.settingsProviderIsOpenRouter ? "" : null
    }
  }, null)]), api_element("label", stc41, [api_text("Model"), api_element("input", {
    classMap: stc5,
    attrs: {
      "data-field": "settingsModelSearch",
      "placeholder": $cmp.modelCatalogCountLabel
    },
    props: {
      "value": $cmp.settingsModelSearch
    },
    key: 114,
    on: _m46 || ($ctx._m46 = {
      "input": api_bind($cmp.handleFieldChange)
    })
  }), api_element("select", {
    classMap: stc42,
    attrs: stc43,
    key: 115,
    on: _m47 || ($ctx._m47 = {
      "change": api_bind($cmp.handleFieldChange)
    })
  }, api_iterator($cmp.settingsModelOptions, function (option) {
    return api_static_fragment($fragment30, api_key(117, option.value), [api_static_part(0, {
      attrs: {
        "value": option.value,
        "selected": option.selected ? "" : null
      }
    }, null), api_static_part(1, null, api_dynamic_text(option.label))]);
  }))]), api_element("label", stc44, [api_text("API token"), api_element("input", {
    classMap: stc5,
    attrs: {
      "type": "password",
      "data-field": "settingsApiKey",
      "placeholder": $cmp.settingsKeyStatus
    },
    props: {
      "value": $cmp.settingsApiKey
    },
    key: 119,
    on: _m48 || ($ctx._m48 = {
      "input": api_bind($cmp.handleFieldChange)
    })
  })]), api_static_fragment($fragment31, 121, [api_static_part(0, {
    on: _m50 || ($ctx._m50 = {
      "click": api_bind($cmp.handleSaveLlmSettings)
    }),
    attrs: {
      "disabled": $cmp.busy.saveSettings ? "" : null
    }
  }, null)])]) : null, $cmp.isAdminOrganizations ? api_element("section", stc45, api_flatten([api_static_fragment($fragment32, 124), api_element("div", stc46, [api_element("input", {
    classMap: stc5,
    attrs: stc47,
    props: {
      "value": $cmp.newOrganizationName
    },
    key: 126,
    on: _m51 || ($ctx._m51 = {
      "input": api_bind($cmp.handleFieldChange)
    })
  }), api_static_fragment($fragment33, 128, [api_static_part(0, {
    on: _m53 || ($ctx._m53 = {
      "click": api_bind($cmp.handleCreateOrganization)
    }),
    attrs: {
      "disabled": $cmp.busy.createOrganization ? "" : null
    }
  }, null)])]), api_iterator($cmp.organizationRows, function (org) {
    return api_static_fragment($fragment34, api_key(130, org.id), [api_static_part(0, {
      on: _m55 || ($ctx._m55 = {
        "click": api_bind($cmp.handleSelectOrganization)
      }),
      className: api_normalize_class_name(org.className),
      attrs: {
        "data-organization-id": org.id
      }
    }, null), api_static_part(1, null, api_dynamic_text(org.name))]);
  })])) : null, $cmp.isAdminUsers ? api_element("section", stc48, api_flatten([api_static_fragment($fragment35, 133), api_element("div", stc49, [api_element("input", {
    classMap: stc5,
    attrs: stc50,
    props: {
      "value": $cmp.newUserName
    },
    key: 135,
    on: _m56 || ($ctx._m56 = {
      "input": api_bind($cmp.handleFieldChange)
    })
  }), api_element("input", {
    classMap: stc5,
    attrs: stc51,
    props: {
      "value": $cmp.newUserEmail
    },
    key: 136,
    on: _m57 || ($ctx._m57 = {
      "input": api_bind($cmp.handleFieldChange)
    })
  }), api_element("input", {
    classMap: stc5,
    attrs: stc52,
    props: {
      "value": $cmp.newUserPassword
    },
    key: 137,
    on: _m58 || ($ctx._m58 = {
      "input": api_bind($cmp.handleFieldChange)
    })
  }), api_static_fragment($fragment36, 139, [api_static_part(0, {
    on: _m60 || ($ctx._m60 = {
      "change": api_bind($cmp.handleFieldChange)
    })
  }, null), api_static_part(1, {
    attrs: {
      "selected": $cmp.newUserRoleIsMember ? "" : null
    }
  }, null), api_static_part(3, {
    attrs: {
      "selected": $cmp.newUserRoleIsAdmin ? "" : null
    }
  }, null), api_static_part(5, {
    attrs: {
      "selected": $cmp.newUserRoleIsViewer ? "" : null
    }
  }, null)]), api_static_fragment($fragment37, 141, [api_static_part(0, {
    on: _m62 || ($ctx._m62 = {
      "click": api_bind($cmp.handleCreateUser)
    }),
    attrs: {
      "disabled": $cmp.busy.createUser ? "" : null
    }
  }, null)])]), api_iterator($cmp.userRows, function (item) {
    return api_static_fragment($fragment38, api_key(143, item.id), [api_static_part(2, null, api_dynamic_text(item.displayName)), api_static_part(4, null, api_dynamic_text(item.email) + " • " + api_dynamic_text(item.role))]);
  })])) : null])]) : null, $cmp.error ? api_static_fragment($fragment39, 145, [api_static_part(1, null, api_dynamic_text($cmp.error))]) : null, $cmp.exportMessage ? api_static_fragment($fragment40, 147, [api_static_part(2, null, api_dynamic_text($cmp.exportMessage)), api_static_part(3, {
    on: _m63 || ($ctx._m63 = {
      "click": api_bind($cmp.handleDismissExportMessage)
    })
  }, null)]) : null]) : null])];
  /*LWC compiler v9.2.2*/
}
var _tmpl = registerTemplate(tmpl);
tmpl.stylesheets = [];
tmpl.stylesheetToken = "lwc-7l57unhr5tc";
tmpl.legacyStylesheetToken = "c-data360Console_data360Console";
if (_implicitStylesheets$5) {
  tmpl.stylesheets.push.apply(tmpl.stylesheets, _implicitStylesheets$5);
}
freezeTemplate(tmpl);

async function request(path, options = {}) {
  const init = {
    method: options.method || "GET",
    headers: await apiHeaders(options.body)
  };
  if (options.body) {
    init.body = JSON.stringify(options.body);
  }
  const response = await fetch(path, init);
  const text = await response.text();
  const json = text ? parseJson(text) : null;
  if (!response.ok) {
    throw new Error(json?.error || json?.message || response.statusText);
  }
  return json;
}
async function apiHeaders(hasBody) {
  const headers = {};
  if (hasBody) {
    headers["Content-Type"] = "application/json";
  }
  if (window.data360Desktop?.apiHeaders) {
    Object.assign(headers, await window.data360Desktop.apiHeaders());
  }
  return headers;
}
function parseJson(text) {
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

const TABS = [{
  id: "chat",
  label: "Chat"
}, {
  id: "templates",
  label: "Templates"
}, {
  id: "review",
  label: "Review"
}, {
  id: "monitors",
  label: "Monitors"
}, {
  id: "audit",
  label: "Audit"
}, {
  id: "admin",
  label: "Admin"
}];
const MODEL_OPTIONS = {
  anthropic: [{
    value: "claude-sonnet-4-6",
    label: "Claude Sonnet 4.6"
  }, {
    value: "claude-opus-4-7",
    label: "Claude Opus 4.7"
  }, {
    value: "claude-haiku-4-5",
    label: "Claude Haiku 4.5"
  }],
  openrouter: [{
    value: "anthropic/claude-sonnet-4.6",
    label: "Anthropic Claude Sonnet 4.6"
  }, {
    value: "anthropic/claude-opus-4.7",
    label: "Anthropic Claude Opus 4.7"
  }, {
    value: "anthropic/claude-haiku-4.5",
    label: "Anthropic Claude Haiku 4.5"
  }]
};
const MODEL_LABELS = new Map([["claude-sonnet-4-6", "Claude Sonnet 4.6"], ["claude-opus-4-7", "Claude Opus 4.7"], ["claude-haiku-4-5", "Claude Haiku 4.5"], ["anthropic/claude-sonnet-4.6", "Claude Sonnet 4.6"], ["anthropic/claude-opus-4.7", "Claude Opus 4.7"], ["anthropic/claude-haiku-4.5", "Claude Haiku 4.5"], ["anthropic/claude-3.5-sonnet", "Claude 3.5 Sonnet"]]);
class Data360Console extends LightningElement {
  constructor(...args) {
    super(...args);
    this.activeTab = "chat";
    this.authReady = false;
    this.setupRequired = false;
    this.user = {
      username: "anonymous",
      authenticated: false,
      authorities: []
    };
    this.setupOrganizationName = "Acme Travel";
    this.setupDisplayName = "";
    this.setupEmail = "";
    this.setupPassword = "";
    this.loginEmail = "";
    this.loginPassword = "";
    this.scenarios = [];
    this.templates = [];
    this.selectedScenarioId = "";
    this.goal = "";
    this.messages = [];
    this.chatMode = "auto";
    this.currentDraft = null;
    this.currentApprovedPlan = null;
    this.currentRun = null;
    this.plannerStatus = "Ready";
    this.selectedStepId = null;
    this.diagnostics = null;
    this.demo = null;
    this.selectedAccountId = null;
    this.monitors = [];
    this.lastMonitorRun = null;
    this.recommendations = [];
    this.recommendationUnavailable = null;
    this.approvalHistory = [];
    this.approvalHistoryUnavailable = null;
    this.llmSettings = null;
    this.settingsProvider = "anthropic";
    this.settingsModel = "claude-sonnet-4-6";
    this.settingsApiKey = "";
    this.settingsModelSearch = "";
    this.providerModelOptions = MODEL_OPTIONS;
    this.mcpSettings = null;
    this.mcpServers = [];
    this.mcpValidation = null;
    this.mcpSaveMessage = "";
    this.selectedMcpServerId = "data360";
    this.activeAdminSection = "mcp";
    this.organizations = [];
    this.selectedAdminOrgId = "";
    this.organizationUsers = [];
    this.newOrganizationName = "";
    this.newUserName = "";
    this.newUserEmail = "";
    this.newUserPassword = "";
    this.newUserRole = "MEMBER";
    this.busy = {};
    this.error = null;
    this.exportMessage = "";
    this.handleHashChange = () => {
      const hash = window.location.hash.replace("#", "");
      if (TABS.some(tab => tab.id === hash)) {
        this.activeTab = hash;
      }
    };
  }
  connectedCallback() {
    const hash = window.location.hash.replace("#", "");
    if (TABS.some(tab => tab.id === hash)) {
      this.activeTab = hash;
    }
    window.addEventListener("hashchange", this.handleHashChange);
    this.loadAuthStatus();
  }
  disconnectedCallback() {
    window.removeEventListener("hashchange", this.handleHashChange);
  }
  get showAuthScreen() {
    return this.authReady && !this.user.authenticated;
  }
  get authSubtitle() {
    return this.setupRequired ? "Create the first organization and owner." : "Sign in to continue.";
  }
  get tabs() {
    return TABS.map(tab => ({
      ...tab,
      href: `#${tab.id}`,
      className: `nav-item ${this.activeTab === tab.id ? "active" : ""}`
    }));
  }
  get isChat() {
    return this.activeTab === "chat";
  }
  get isTemplates() {
    return this.activeTab === "templates";
  }
  get isReview() {
    return this.activeTab === "review";
  }
  get isMonitors() {
    return this.activeTab === "monitors";
  }
  get isAudit() {
    return this.activeTab === "audit";
  }
  get isAdmin() {
    return this.activeTab === "admin";
  }
  get userLabel() {
    return this.user.authenticated ? this.user.username : "Not signed in";
  }
  get modelLabel() {
    if (!this.llmSettings) return "Model not configured";
    const configured = this.llmSettings.apiKeyConfigured ? "ready" : "missing token";
    return `${providerLabel(this.llmSettings.provider)} / ${modelLabel(this.llmSettings.model)} (${configured})`;
  }
  get compactModelLabel() {
    if (!this.llmSettings) return "Model not configured";
    const configured = this.llmSettings.apiKeyConfigured ? "ready" : "missing token";
    return `${modelLabel(this.llmSettings.model)} (${configured})`;
  }
  get settingsProviderIsAnthropic() {
    return this.settingsProvider === "anthropic";
  }
  get settingsProviderIsOpenRouter() {
    return this.settingsProvider === "openrouter";
  }
  get settingsModelOptions() {
    const allOptions = this.providerModelOptions[this.settingsProvider] || MODEL_OPTIONS[this.settingsProvider] || MODEL_OPTIONS.anthropic;
    const query = this.settingsModelSearch.trim().toLowerCase();
    const options = query ? allOptions.filter(option => `${option.label} ${option.value}`.toLowerCase().includes(query)) : allOptions;
    const selectedModel = this.settingsModel || options[0]?.value || "";
    const hasSelected = allOptions.some(option => option.value === selectedModel);
    const selectedOption = allOptions.find(option => option.value === selectedModel);
    const visibleOptions = hasSelected || !selectedModel ? options : [{
      value: selectedModel,
      label: `Current: ${modelLabel(selectedModel)}`
    }, ...options];
    const withSelected = selectedOption && !visibleOptions.some(option => option.value === selectedOption.value) ? [selectedOption, ...visibleOptions] : visibleOptions;
    return withSelected.map(option => ({
      ...option,
      selected: option.value === selectedModel
    }));
  }
  get modelCatalogCountLabel() {
    const count = (this.providerModelOptions[this.settingsProvider] || []).length;
    if (!count) return "Loading models";
    return `${count} available models`;
  }
  get settingsKeyStatus() {
    if (!this.llmSettings?.apiKeyConfigured) return "No token saved";
    return this.llmSettings.apiKeyLast4 ? `Saved token ending ${this.llmSettings.apiKeyLast4}` : "Token saved";
  }
  get organizationRows() {
    return (this.organizations || []).map(org => ({
      ...org,
      className: `org-row ${org.id === this.selectedAdminOrgId ? "selected" : ""}`
    }));
  }
  get userRows() {
    return this.organizationUsers || [];
  }
  get adminSections() {
    return [{
      id: "mcp",
      label: "MCP servers"
    }, {
      id: "model",
      label: "Models"
    }, {
      id: "organizations",
      label: "Organizations"
    }, {
      id: "users",
      label: "Users"
    }].map(section => ({
      ...section,
      className: `settings-nav-item ${this.activeAdminSection === section.id ? "active" : ""}`
    }));
  }
  get isAdminMcp() {
    return this.activeAdminSection === "mcp";
  }
  get isAdminModel() {
    return this.activeAdminSection === "model";
  }
  get isAdminOrganizations() {
    return this.activeAdminSection === "organizations";
  }
  get isAdminUsers() {
    return this.activeAdminSection === "users";
  }
  get selectedMcpServer() {
    return this.mcpServers.find(server => server.id === this.selectedMcpServerId) || this.mcpServers[0] || null;
  }
  get selectedMcpServerRows() {
    return (this.mcpServers || []).map(server => ({
      ...server,
      className: `mcp-server-option ${server.id === this.selectedMcpServerId ? "active" : ""}`,
      statusLabel: this.mcpServerStatusLabel(server)
    }));
  }
  get mcpValidationClass() {
    const status = this.mcpValidation?.status || "passed";
    return `settings-feedback ${status === "failed" ? "failed" : status === "skipped" ? "neutral" : "passed"}`;
  }
  get mcpValidationRows() {
    return (this.mcpValidation?.servers || []).map(server => ({
      ...server,
      className: `validation-row ${server.status}`,
      toolSummary: server.tools?.length ? server.tools.join(", ") : ""
    }));
  }
  get selectedMcpIsStdio() {
    return this.selectedMcpServer?.transport !== "streamable-http";
  }
  get selectedMcpIsHttp() {
    return this.selectedMcpServer?.transport === "streamable-http";
  }
  get mcpStdioButtonClass() {
    return `transport-option ${this.selectedMcpIsStdio ? "active" : ""}`;
  }
  get mcpHttpButtonClass() {
    return `transport-option ${this.selectedMcpIsHttp ? "active" : ""}`;
  }
  mcpServerStatusLabel(server) {
    const validation = (this.mcpValidation?.servers || []).find(result => result.id === server.id);
    if (validation) {
      return validation.status === "passed" ? `Validated (${validation.toolCount} tools)` : validation.status === "failed" ? "Validation failed" : "Skipped";
    }
    return server.enabled ? "Enabled" : "Disabled";
  }
  get newUserRoleIsMember() {
    return this.newUserRole === "MEMBER";
  }
  get newUserRoleIsAdmin() {
    return this.newUserRole === "ADMIN";
  }
  get newUserRoleIsViewer() {
    return this.newUserRole === "VIEWER";
  }
  async loadAuthStatus() {
    this.error = null;
    try {
      const status = await request("/api/auth/status");
      this.user = status.user || this.user;
      this.setupRequired = Boolean(status.setupRequired);
      this.authReady = true;
      if (this.user.authenticated) {
        await this.loadAll();
      }
    } catch (error) {
      this.authReady = true;
      this.error = error.message;
    }
  }
  async loadAll() {
    await Promise.all([this.loadDemo(), this.loadScenarios(), this.loadTemplates(), this.loadDiagnostics(), this.loadMonitors(), this.loadRecommendations(), this.loadLlmSettings(), this.loadMcpSettings(), this.loadOrganizations()]);
  }
  async loadDemo() {
    try {
      this.demo = await request("/api/demo/dormant-revenue-recovery");
      this.selectedAccountId ||= this.demo.accounts?.[0]?.id;
    } catch {
      this.demo = null;
    }
  }
  async loadScenarios() {
    try {
      this.scenarios = await request("/api/scenarios");
      const first = this.scenarios[0];
      this.selectedScenarioId = this.selectedScenarioId || first?.id || "";
      this.goal = this.goal || first?.defaultUtterances?.[0] || "";
    } catch (error) {
      this.error = error.message;
    }
  }
  async loadTemplates() {
    try {
      this.templates = await request("/api/library");
    } catch {
      this.templates = [];
    }
  }
  async loadDiagnostics() {
    try {
      this.diagnostics = await request("/api/data360/diagnostics");
    } catch (error) {
      this.diagnostics = {
        status: "failed",
        mode: "connect",
        configured: false,
        error: error.message
      };
    }
  }
  async loadMonitors() {
    try {
      this.monitors = await request("/api/monitors");
    } catch {
      this.monitors = [];
    }
  }
  async loadRecommendations() {
    try {
      this.recommendations = await request("/api/monitors/recommendations");
      this.recommendationUnavailable = null;
    } catch (error) {
      this.recommendations = [];
      this.recommendationUnavailable = error.message;
    }
  }
  async loadLlmSettings() {
    try {
      this.llmSettings = await request("/api/llm-settings");
      this.settingsProvider = this.llmSettings.provider || "anthropic";
      this.settingsModel = this.llmSettings.model || this.settingsModel;
      await this.loadModelCatalog(this.settingsProvider);
    } catch (error) {
      this.error = error.message;
    }
  }
  async loadModelCatalog(provider) {
    try {
      const models = await request(`/api/llm-settings/models?provider=${encodeURIComponent(provider)}`);
      const options = (models || []).map(model => ({
        value: model.id,
        label: model.label || model.id
      }));
      if (options.length) {
        this.providerModelOptions = {
          ...this.providerModelOptions,
          [provider]: options
        };
      }
    } catch {
      this.providerModelOptions = {
        ...this.providerModelOptions,
        [provider]: MODEL_OPTIONS[provider] || MODEL_OPTIONS.anthropic
      };
    }
  }
  async loadMcpSettings() {
    try {
      this.mcpSettings = await request("/api/mcp-settings");
      this.mcpServers = (this.mcpSettings.servers || []).map(prepareMcpServer);
      if (!this.mcpServers.some(server => server.id === this.selectedMcpServerId)) {
        this.selectedMcpServerId = this.mcpServers[0]?.id || "";
      }
    } catch (error) {
      this.error = error.message;
    }
  }
  async loadOrganizations() {
    try {
      this.organizations = await request("/api/organizations");
      this.selectedAdminOrgId = this.selectedAdminOrgId || this.user.organizationId || this.organizations[0]?.id || "";
      if (this.selectedAdminOrgId) {
        await this.loadUsers();
      }
    } catch {
      this.organizations = [];
      this.organizationUsers = [];
    }
  }
  async loadUsers() {
    if (!this.selectedAdminOrgId) return;
    this.organizationUsers = await request(`/api/organizations/${this.selectedAdminOrgId}/users`);
  }
  handleTab(event) {
    event.preventDefault();
    const tab = event.currentTarget.dataset.tab;
    this.activeTab = tab;
    if (window.location.hash !== `#${tab}`) {
      history.pushState(null, "", `#${tab}`);
    }
  }
  handleFieldChange(event) {
    const field = event.target.dataset.field;
    this[field] = event.target.value;
    if (field === "settingsProvider") {
      this.settingsModel = defaultModelForProvider(this.settingsProvider);
      this.settingsModelSearch = "";
      this.loadModelCatalog(this.settingsProvider);
    }
  }
  handleAdminSection(event) {
    this.activeAdminSection = event.currentTarget.dataset.section;
  }
  handleLoginKeydown(event) {
    if (event.key !== "Enter") return;
    event.preventDefault();
    this.handleLogin();
  }
  async handleBootstrap() {
    this.setBusy("auth", true);
    this.error = null;
    try {
      const status = await request("/api/auth/bootstrap", {
        method: "POST",
        body: {
          organizationName: this.setupOrganizationName,
          displayName: this.setupDisplayName,
          email: this.setupEmail,
          password: this.setupPassword
        }
      });
      this.afterAuth(status);
    } catch (error) {
      this.error = error.message;
    } finally {
      this.setBusy("auth", false);
    }
  }
  async handleLogin() {
    this.setBusy("auth", true);
    this.error = null;
    try {
      const status = await request("/api/auth/login", {
        method: "POST",
        body: {
          email: this.loginEmail,
          password: this.loginPassword
        }
      });
      this.afterAuth(status);
    } catch (error) {
      this.error = error.message;
    } finally {
      this.setBusy("auth", false);
    }
  }
  async afterAuth(status) {
    this.user = status.user;
    this.setupRequired = false;
    this.loginPassword = "";
    this.setupPassword = "";
    this.messages = [{
      role: "assistant",
      text: "What should Data 360 set up?",
      meta: "I will draft a governed PlanSpec for approval before execution."
    }];
    await this.loadAll();
  }
  async handleLogout() {
    await request("/api/auth/logout", {
      method: "POST"
    });
    this.user = {
      username: "anonymous",
      authenticated: false,
      authorities: []
    };
    this.currentDraft = null;
    this.currentApprovedPlan = null;
    this.currentRun = null;
    this.messages = [];
    await this.loadAuthStatus();
  }
  async handleChatMessage(event) {
    const text = event.detail.text;
    const mode = event.detail.mode || this.chatMode || "auto";
    this.messages = [...this.messages, {
      role: "user",
      text
    }];
    this.goal = text;
    if (mode !== "plan" && (mode === "execute" || !shouldDraftPlan(text))) {
      await this.handleConversationalChat(text);
      return;
    }
    this.messages = [...this.messages, {
      role: "assistant",
      text: "I am drafting a governed PlanSpec for that goal.",
      meta: mode === "plan" ? "Plan mode" : "Planning"
    }];
    const draft = await this.handleDraftPlan();
    if (draft?.plan) {
      const count = draft.plan.steps?.length || 0;
      this.messages = [...this.messages, {
        role: "assistant",
        text: `I drafted a PlanSpec with ${count} ${count === 1 ? "step" : "steps"}. Review it before running.`,
        meta: draft.validation?.ok ? "Validation passed." : "Validation needs attention."
      }];
    }
  }
  handleChatModeChange(event) {
    const mode = event.detail.mode;
    if (!["auto", "plan", "execute"].includes(mode)) return;
    this.chatMode = mode;
  }
  async handleConversationalChat(text) {
    this.setBusy("chat", true);
    this.plannerStatus = "Thinking";
    this.error = null;
    try {
      const response = await request("/api/chat", {
        method: "POST",
        body: {
          message: text,
          mode: this.chatMode
        }
      });
      this.messages = [...this.messages, {
        role: "assistant",
        text: response.text || "I am the Data 360 Agent Console assistant.",
        meta: response.model ? `${providerLabel(response.provider)} / ${modelLabel(response.model)}` : "Chat",
        trace: response.trace || []
      }];
      this.plannerStatus = "Ready";
    } catch (error) {
      this.error = error.message;
      this.messages = [...this.messages, {
        role: "assistant",
        text: "I could not get a response from the configured model.",
        meta: error.message
      }];
      this.plannerStatus = "Error";
    } finally {
      this.setBusy("chat", false);
    }
  }
  handleScenarioChange(event) {
    const scenario = this.scenarios.find(item => item.id === event.detail.scenarioId);
    this.selectedScenarioId = scenario?.id || "";
    this.goal = scenario?.defaultUtterances?.[0] || scenario?.name || this.goal;
    this.resetCurrentDraft();
  }
  handleGoalChange(event) {
    this.goal = event.detail.goal;
    this.resetCurrentDraft();
  }
  handleUseTemplate(event) {
    const template = event.detail.template;
    this.goal = template ? `${template.title}: ${template.outcome || template.summary || ""}` : this.goal;
    this.activeTab = "chat";
    this.messages = [...this.messages, {
      role: "assistant",
      text: this.goal,
      meta: "Template loaded as the next chat goal."
    }];
  }
  async handleInstantiateTemplate(event) {
    const templateId = event.detail.templateId;
    if (!templateId) return;
    this.setBusy("instantiateTemplate", true);
    try {
      this.currentDraft = await request(`/api/library/${templateId}/plans`, {
        method: "POST",
        body: {
          context: {
            org: this.user.organizationName || "default-org",
            dataspace: "default",
            environment: "sandbox"
          }
        }
      });
      this.currentApprovedPlan = null;
      this.selectedStepId = this.currentDraft.plan.steps?.[0]?.id || null;
      this.activeTab = "review";
    } catch (error) {
      this.error = error.message;
    } finally {
      this.setBusy("instantiateTemplate", false);
    }
  }
  resetCurrentDraft() {
    this.currentDraft = null;
    this.currentApprovedPlan = null;
    this.currentRun = null;
    this.selectedStepId = null;
    this.approvalHistory = [];
    this.approvalHistoryUnavailable = null;
    this.plannerStatus = "Ready";
  }
  async handleDraftPlan() {
    const scenario = this.scenarios.find(item => item.id === this.selectedScenarioId) || this.scenarios[0];
    this.setBusy("draftPlan", true);
    this.plannerStatus = "Planning";
    this.error = null;
    try {
      this.currentDraft = await request("/api/plans", {
        method: "POST",
        body: {
          scenarioId: scenario?.id,
          goal: this.goal,
          context: {
            org: this.user.organizationName || "default-org",
            dataspace: "default",
            environment: "sandbox"
          }
        }
      });
      this.currentApprovedPlan = null;
      this.currentRun = null;
      this.selectedStepId = this.currentDraft.plan.steps?.[0]?.id || null;
      this.approvalHistory = [];
      this.approvalHistoryUnavailable = null;
      this.plannerStatus = this.currentDraft.validation?.ok ? "Ready" : "Review";
      return this.currentDraft;
    } catch (error) {
      this.error = error.message;
      this.plannerStatus = "Error";
      this.messages = [...this.messages, {
        role: "assistant",
        text: "I could not draft the PlanSpec.",
        meta: error.message
      }];
      return null;
    } finally {
      this.setBusy("draftPlan", false);
    }
  }
  async handleStartPlan() {
    if (!this.currentDraft || !this.currentApprovedPlan) return;
    this.setBusy("startPlan", true);
    this.plannerStatus = "Running";
    try {
      const response = await request(`/api/plans/${this.currentDraft.plan.id}/runs`, {
        method: "POST",
        body: {
          artifactId: this.currentApprovedPlan.artifactId,
          planHash: this.currentApprovedPlan.planHash
        }
      });
      if (!response.id) {
        this.currentDraft = response;
        this.currentApprovedPlan = null;
        this.currentRun = null;
        this.plannerStatus = "Review";
        return;
      }
      this.currentRun = response;
      this.messages = [...this.messages, {
        role: "assistant",
        text: `Run ${response.id} started.`,
        meta: response.status
      }];
      await this.pollRun(response.id);
      await this.loadMonitors();
    } catch (error) {
      this.error = error.message;
      this.plannerStatus = "Error";
    } finally {
      this.setBusy("startPlan", false);
    }
  }
  async handleApprovePlan() {
    if (!this.currentDraft) return;
    this.setBusy("approvePlan", true);
    this.plannerStatus = "Approving";
    try {
      const response = await request(`/api/plans/${this.currentDraft.plan.id}/approve`, {
        method: "POST"
      });
      if (response.artifactType !== "approved-executable-plan") {
        this.currentDraft = response;
        this.currentApprovedPlan = null;
        this.currentRun = null;
        this.plannerStatus = "Review";
        return;
      }
      this.currentApprovedPlan = response;
      this.plannerStatus = "Approved";
      this.messages = [...this.messages, {
        role: "assistant",
        text: "PlanSpec approved as an executable artifact.",
        meta: response.artifactId || response.planHash
      }];
    } catch (error) {
      this.error = error.message;
      this.plannerStatus = "Error";
    } finally {
      this.setBusy("approvePlan", false);
    }
  }
  async handleApproveStep(event) {
    if (!this.currentRun) return;
    this.plannerStatus = "Approving";
    this.currentRun = await request(`/api/runs/${this.currentRun.id}/steps/${event.detail.stepId}/approve`, {
      method: "POST"
    });
    await this.pollRun(this.currentRun.id);
    await this.loadMonitors();
  }
  async pollRun(runId) {
    for (let i = 0; i < 20; i += 1) {
      await delay(150);
      this.currentRun = await request(`/api/runs/${runId}`);
      if (["WAITING_APPROVAL", "SUCCEEDED", "FAILED"].includes(this.currentRun.status)) {
        await this.loadApprovalHistory(runId);
        this.plannerStatus = this.currentRun.status;
        return;
      }
    }
    await this.loadApprovalHistory(runId);
    this.plannerStatus = this.currentRun.status;
  }
  async loadApprovalHistory(runId) {
    try {
      this.approvalHistory = await request(`/api/runs/${runId}/approvals`);
      this.approvalHistoryUnavailable = null;
    } catch (error) {
      this.approvalHistory = [];
      this.approvalHistoryUnavailable = error.message;
    }
  }
  handleSelectStep(event) {
    this.selectedStepId = event.detail.stepId;
  }
  async handleSmokeData360() {
    this.setBusy("smokeData360", true);
    try {
      this.diagnostics = await request("/api/data360/diagnostics/smoke", {
        method: "POST"
      });
    } finally {
      this.setBusy("smokeData360", false);
    }
  }
  async handleRunMonitor(event) {
    try {
      this.lastMonitorRun = await request(`/api/monitors/${event.detail.monitorId}/run-now`, {
        method: "POST"
      });
      await this.loadMonitors();
      await this.loadRecommendations();
    } catch (error) {
      this.lastMonitorRun = {
        status: "ERROR",
        recommendation: error.message,
        observedValue: 0,
        thresholdBreached: true
      };
    }
  }
  async handleRecommendationTransition(event) {
    const {
      recommendationId,
      transition
    } = event.detail;
    try {
      await request(`/api/monitors/recommendations/${recommendationId}/${transition}`, {
        method: "POST"
      });
      await this.loadRecommendations();
    } catch (error) {
      this.recommendationUnavailable = error.message;
    }
  }
  async handleSaveLlmSettings() {
    this.setBusy("saveSettings", true);
    this.error = null;
    try {
      const body = {
        provider: this.settingsProvider,
        model: this.settingsModel,
        apiKey: this.settingsApiKey
      };
      this.llmSettings = await request("/api/llm-settings", {
        method: "PUT",
        body
      });
      this.settingsApiKey = "";
    } catch (error) {
      this.error = error.message;
    } finally {
      this.setBusy("saveSettings", false);
    }
  }
  handleMcpEnabledChange(event) {
    const serverId = event.target.dataset.serverId;
    this.mcpSaveMessage = "";
    this.mcpValidation = null;
    this.mcpServers = this.mcpServers.map(server => server.id === serverId ? {
      ...server,
      enabled: event.target.checked
    } : server);
  }
  handleSelectMcpServer(event) {
    this.selectedMcpServerId = event.currentTarget.dataset.serverId;
  }
  handleMcpFieldChange(event) {
    const serverId = event.target.dataset.serverId;
    const field = event.target.dataset.field;
    this.mcpSaveMessage = "";
    this.mcpValidation = null;
    this.mcpServers = this.mcpServers.map(server => server.id === serverId ? {
      ...server,
      [field]: event.target.value,
      commandConfigured: field === "command" ? Boolean(event.target.value?.trim()) : server.commandConfigured
    } : server);
  }
  handleMcpTransportSelect(event) {
    const serverId = event.currentTarget.dataset.serverId;
    const transport = event.currentTarget.dataset.transport;
    this.mcpSaveMessage = "";
    this.mcpValidation = null;
    this.mcpServers = this.mcpServers.map(server => server.id === serverId ? {
      ...server,
      transport
    } : server);
  }
  handleAddMcpArgument(event) {
    const serverId = event.currentTarget.dataset.serverId;
    this.updateMcpServer(serverId, server => ({
      ...server,
      arguments: [...server.arguments, keyedValue("")]
    }));
  }
  handleMcpArgumentChange(event) {
    this.updateMcpListValue(event, "arguments", "value");
  }
  handleRemoveMcpArgument(event) {
    this.removeMcpListItem(event, "arguments");
  }
  handleAddMcpEnvironment(event) {
    const serverId = event.currentTarget.dataset.serverId;
    this.updateMcpServer(serverId, server => ({
      ...server,
      environment: [...server.environment, keyedPair("", "")]
    }));
  }
  handleMcpEnvironmentChange(event) {
    this.updateMcpListValue(event, "environment", event.target.dataset.field);
  }
  handleRemoveMcpEnvironment(event) {
    this.removeMcpListItem(event, "environment");
  }
  handleAddMcpPassthrough(event) {
    const serverId = event.currentTarget.dataset.serverId;
    this.updateMcpServer(serverId, server => ({
      ...server,
      environmentPassthrough: [...server.environmentPassthrough, keyedValue("")]
    }));
  }
  handleMcpPassthroughChange(event) {
    this.updateMcpListValue(event, "environmentPassthrough", "value");
  }
  handleRemoveMcpPassthrough(event) {
    this.removeMcpListItem(event, "environmentPassthrough");
  }
  updateMcpListValue(event, listName, fieldName) {
    const serverId = event.target.dataset.serverId;
    const itemId = event.target.dataset.itemId;
    this.updateMcpServer(serverId, server => ({
      ...server,
      [listName]: server[listName].map(item => item.id === itemId ? {
        ...item,
        [fieldName]: event.target.value
      } : item)
    }));
  }
  removeMcpListItem(event, listName) {
    const serverId = event.currentTarget.dataset.serverId;
    const itemId = event.currentTarget.dataset.itemId;
    this.updateMcpServer(serverId, server => ({
      ...server,
      [listName]: server[listName].filter(item => item.id !== itemId)
    }));
  }
  updateMcpServer(serverId, updater) {
    this.mcpSaveMessage = "";
    this.mcpValidation = null;
    this.mcpServers = this.mcpServers.map(server => server.id === serverId ? updater(server) : server);
  }
  async handleSaveMcpSettings() {
    this.setBusy("saveMcpSettings", true);
    this.error = null;
    this.mcpSaveMessage = "";
    this.mcpValidation = null;
    try {
      this.mcpSettings = await request("/api/mcp-settings", {
        method: "PUT",
        body: this.mcpSettingsRequestBody()
      });
      this.mcpServers = (this.mcpSettings.servers || []).map(prepareMcpServer);
      this.mcpSaveMessage = "MCP settings saved. Validating launch configuration...";
      this.mcpValidation = await request("/api/mcp-settings/validate", {
        method: "POST"
      });
      this.mcpSaveMessage = "MCP settings saved.";
    } catch (error) {
      this.error = error.message;
    } finally {
      this.setBusy("saveMcpSettings", false);
    }
  }
  mcpSettingsRequestBody() {
    return {
      servers: this.mcpServers.map(server => ({
        id: server.id,
        name: server.name,
        enabled: server.enabled,
        transport: server.transport,
        command: server.command,
        endpoint: server.endpoint,
        arguments: server.arguments.map(item => item.value).filter(Boolean),
        environment: server.environment.filter(item => item.key?.trim()).map(item => ({
          key: item.key,
          value: item.value
        })),
        environmentPassthrough: server.environmentPassthrough.map(item => item.value).filter(Boolean),
        workingDirectory: server.workingDirectory
      }))
    };
  }
  async handleCreateOrganization() {
    if (!this.newOrganizationName.trim()) return;
    this.setBusy("createOrganization", true);
    try {
      const org = await request("/api/organizations", {
        method: "POST",
        body: {
          name: this.newOrganizationName
        }
      });
      this.newOrganizationName = "";
      this.selectedAdminOrgId = org.id;
      await this.loadOrganizations();
    } catch (error) {
      this.error = error.message;
    } finally {
      this.setBusy("createOrganization", false);
    }
  }
  handleSelectOrganization(event) {
    this.selectedAdminOrgId = event.currentTarget.dataset.organizationId;
    this.loadUsers();
  }
  async handleCreateUser() {
    if (!this.selectedAdminOrgId) return;
    this.setBusy("createUser", true);
    try {
      await request(`/api/organizations/${this.selectedAdminOrgId}/users`, {
        method: "POST",
        body: {
          displayName: this.newUserName,
          email: this.newUserEmail,
          password: this.newUserPassword,
          role: this.newUserRole
        }
      });
      this.newUserName = "";
      this.newUserEmail = "";
      this.newUserPassword = "";
      await this.loadUsers();
    } catch (error) {
      this.error = error.message;
    } finally {
      this.setBusy("createUser", false);
    }
  }
  async handleExportPlanSpec() {
    if (!this.currentDraft?.plan?.id) return;
    this.setBusy("exportPlanSpec", true);
    try {
      const archive = await request(`/api/plans/${this.currentDraft.plan.id}/export`);
      await exportJson(`planspec-${this.currentDraft.plan.id}.json`, archive);
      this.exportMessage = "PlanSpec exported.";
    } catch (error) {
      this.error = error.message;
    } finally {
      this.setBusy("exportPlanSpec", false);
    }
  }
  handleDismissExportMessage() {
    this.exportMessage = "";
  }
  setBusy(key, value) {
    this.busy = {
      ...this.busy,
      [key]: value
    };
  }
  /*LWC compiler v9.2.2*/
}
registerDecorators(Data360Console, {
  fields: ["activeTab", "authReady", "setupRequired", "user", "setupOrganizationName", "setupDisplayName", "setupEmail", "setupPassword", "loginEmail", "loginPassword", "scenarios", "templates", "selectedScenarioId", "goal", "messages", "chatMode", "currentDraft", "currentApprovedPlan", "currentRun", "plannerStatus", "selectedStepId", "diagnostics", "demo", "selectedAccountId", "monitors", "lastMonitorRun", "recommendations", "recommendationUnavailable", "approvalHistory", "approvalHistoryUnavailable", "llmSettings", "settingsProvider", "settingsModel", "settingsApiKey", "settingsModelSearch", "providerModelOptions", "mcpSettings", "mcpServers", "mcpValidation", "mcpSaveMessage", "selectedMcpServerId", "activeAdminSection", "organizations", "selectedAdminOrgId", "organizationUsers", "newOrganizationName", "newUserName", "newUserEmail", "newUserPassword", "newUserRole", "busy", "error", "exportMessage", "handleHashChange"]
});
const __lwc_component_class_internal = registerComponent(Data360Console, {
  tmpl: _tmpl,
  sel: "c-data360console",
  apiVersion: 66
});
function shouldDraftPlan(text) {
  const value = (text || "").toLowerCase();
  const planSignals = ["planspec", "plan spec", "draft a plan", "make a plan", "set up", "setup", "configure", "create", "build", "deploy", "run", "execute", "activate", "delete", "update", "change", "monitor", "ingest", "unify", "segment customers", "identity resolution", "calculated insight"];
  return planSignals.some(signal => value.includes(signal));
}
function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}
async function exportJson(defaultFileName, payload) {
  if (window.data360Desktop?.exportJson) {
    return window.data360Desktop.exportJson({
      defaultFileName,
      payload
    });
  }
  const blob = new Blob([JSON.stringify(payload, null, 2)], {
    type: "application/json"
  });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = defaultFileName;
  link.click();
  URL.revokeObjectURL(url);
  return {
    canceled: false
  };
}
function defaultModelForProvider(provider) {
  return (MODEL_OPTIONS[provider] || MODEL_OPTIONS.anthropic)[0]?.value || "";
}
function providerLabel(provider) {
  if (provider === "mcp") return "Data 360 MCP";
  return provider === "openrouter" ? "OpenRouter" : "Anthropic";
}
function modelLabel(model) {
  return MODEL_LABELS.get(model) || model || "Model not configured";
}
let rowId = 0;
function nextRowId() {
  rowId += 1;
  return `row-${rowId}`;
}
function keyedValue(value) {
  return {
    id: nextRowId(),
    value: value || ""
  };
}
function keyedPair(key, value) {
  return {
    id: nextRowId(),
    key: key || "",
    value: value || ""
  };
}
function prepareMcpServer(server) {
  return {
    ...server,
    name: server.name || server.label || server.id,
    transport: server.transport || "stdio",
    endpoint: server.endpoint || "",
    command: server.command || "",
    arguments: (server.arguments || []).map(keyedValue),
    environment: (server.environment || []).map(item => keyedPair(item.key, item.value)),
    environmentPassthrough: (server.environmentPassthrough || []).map(keyedValue),
    workingDirectory: server.workingDirectory || ""
  };
}

const mount = document.getElementById("app");
mount.replaceChildren(createElement("c-data360-console", {
  is: __lwc_component_class_internal
}));
