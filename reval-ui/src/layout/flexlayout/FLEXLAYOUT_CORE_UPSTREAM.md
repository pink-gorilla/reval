# `layout.flexlayout.core` — changes for upstream (`ui-layout`)

This file documents every deliberate change from the stock **`org.pinkgorilla/flexlayout`** `layout/flexlayout/core.cljs` (under [pink-gorilla/ui-layout](https://github.com/pink-gorilla/ui-layout), `flexlayout/` subtree) as currently **vendored** in this repo at:

`reval-ui/src/layout/flexlayout/core.cljs`

Use this checklist when opening a PR against `ui-layout` so nothing is dropped.

---

## Baseline

- **Upstream module**: `flexlayout/src/layout/flexlayout/core.cljs` (git dep `org.pinkgorilla/flexlayout` from ui-layout).
- **Purpose here**: classpath override (`reval-ui/src` before dependency jars) so the REPL and nested file layouts work without waiting for a published flexlayout version.

---

## 1. Namespace docstring

- Extended to mention:
  - `:dock` on `add-node`
  - Optional `layout-state` / `selection-atom` on `flex-layout` for nested layouts

---

## 2. New requires

| Symbol | Purpose |
|--------|--------|
| `["react" :as react]` | `useMemo` for stable `factory` / `onAction` in `flex-layout` |
| `RowNode` (from `flexlayout-react`) | `add-node-tree` — build row/tabset subtree before `drop` |
| `DockLocation` | `:dock` on `add-node` and `add-node-tree` |

---

## 3. `subscribe-state` refactor

- **`subscribe-state-in [data-a-atom id]`** (new): reaction over an arbitrary `data-a` atom (supports nested layouts sharing the same `data-a` as the root).
- **`subscribe-state [id]`** (kept): backward-compatible wrapper — uses `(:data-a @state-a)`.

---

## 4. Component factory

- **`make-component-factory [layout-state-atom _selection-atom]`** (new): returns the `fn` passed to FlexLayout’s `factory`.
  - Resolves `data-a` from **`layout-state-atom`** (`(:data-a @layout-state-atom)`), not hard-coded `state-a`.
  - Passes **`layout-state-atom`** through in opts as `:layout-state-atom` (optional; consumers like REPL sublayouts can use it).

Previously: a single top-level `component-factory` bound only to global `state-a`.

---

## 5. Action handling

- **`data-a-from-layout-state`** (private): `(:data-a @layout-state-atom)`.

- **`make-handle-action [layout-state-atom selection-atom]`** (new): builds the `onAction` callback.
  - **SELECT_TAB**: `reset!` on **`selection-atom`** (not hard-coded `selected-id-a`) so nested layouts can share the root selection atom or use their own.
  - **DELETE_TAB**: `dissoc` on the **`data-a`** from `layout-state-atom`. Additionally, if the removed entry is **`{:kind :repl-file ...}`** (app-specific), remove **`(:code-id v)`** and **`(:nb-id v)`** from `data-a` so grouped inner tabs do not leak after the outer tab closes.

Previously: top-level `handle-action` only used global `state-a` / `selected-id-a`, and DELETE_TAB only dissociated the deleted tab id.

**Note for upstream:** The `:repl-file` cleanup is REPL-specific. For a generic `ui-layout` library, consider either:

- a multimethod / hook `(on-delete-tab data-a cell-id entry)`, or
- documenting that apps should use `onAction` wrapping — and keep only the parameterized `layout-state` + `selection-atom` in core.

---

## 6. `flex-layout` UI component

New optional props (defaults preserve old behavior):

| Prop | Default | Role |
|------|---------|------|
| `:layout-state` | `state-a` | Atom merged in `Layout` `:ref` with `:layout`, `:model`, `:category`, `:model-name`, `:data-a` |
| `:selection-atom` | `selected-id-a` | Atom updated on tab select |

Implementation details:

- **`react/useMemo`** for factory and handler so references stay stable when `layout-state` / `selection-atom` identities are stable.
- **`:ref` callback**: **`swap! layout-state merge {...}`** instead of **`reset! state-a`** so nested layouts use their **own** atom without clobbering the root.
- **`:data-a`**: `(or (:data-a @layout-state) (r/atom (or data {})))` so a nested layout can pre-bind the **same** `data-a` as the root before the ref runs (shared tab state).

---

## 7. `add-node`

- **`^js` type hint** on `model` for ClojureScript inference on `.doAction`.
- **`{:keys [id state dock] :or {dock :center}}`**: optional **`dock`** (keyword `:center` | `:right` | `:left` | `:top` | `:bottom`).
- Tab JSON passed to FlexLayout: **`(dissoc node :state :dock)`** so those keys are not serialized as tab attributes.
- Behavior:
  - **`:center`**: `(.addTabToTabSet ^js layout tsid node-js)` when root `layout` ref exists.
  - **Otherwise**: `(.doAction model (Actions.addNode node-js tsid loc -1 true))` with `DockLocation` enum from `dock`.

**Still uses root `state-a`** for `model`, `layout`, `data-a` (intentional: “add tab in root layout” API).

---

## 8. `add-node-tree` (new)

Public helper for docking a **full row subtree** in one go (shape compatible with `Model` layout JSON: `row` → `tabset` → `tab`, etc.).

- **Why:** `Actions.addNode` in flexlayout-react always wraps JSON in a **`TabNode`**; row/tabset JSON must be built with **`RowNode.fromJson`** and attached via **`TabSetNode.drop`**.
- **Args:**
  - `row-json` — Clojure map (passed through `clj->js`).
  - `{:keys [tab-states dock] :or {dock :right}}` — merge each `tab-states` entry into root **`data-a`**; **`dock`** selects side vs active tabset (reuse **`dock-location-kw->enum`**).
- After drop: **`(.updateIdMap ^js model)`** so internal id maps stay consistent.

**Still uses root `state-a`** (same assumption as `add-node`).

---

## 9. `save-layout` and `flexlayout-page`

- **`^js`** hint on `model` where used for interop (consistency / inference).

---

## 10. What did *not* change (for parity checks)

- `state-a`, `selected-id-a`, `save-layout`, `layout-data-model-a`, `flexlayout-model-load`, `flexlayout-with-header`, `flexlayout-only`, `flexlayout-page` — same overall roles as upstream.
- CSS link path `/r/flexlayout-react/style/light.css` unchanged (app may still override via wrapper).

---

## Porting checklist (PR to `ui-layout` / `flexlayout`)

1. [ ] Add `react` dependency usage in `flex-layout` (or replace `useMemo` with library-consistent hook helper if `ui-layout` standardises on UIX hooks only).
2. [ ] Export/document **`subscribe-state-in`**, **`make-component-factory`**, **`make-handle-action`**, or keep them private and only expose **`flex-layout`** options.
3. [ ] Decide fate of **`:repl-file` DELETE_TAB** logic: merge as-is, behind a flag, or replace with a generic hook.
4. [ ] Add **`add-node-tree`** + **`RowNode`** require + tests or demo snippet.
5. [ ] **`add-node`**: document `:dock` and `clj->js` stripping of `:state` / `:dock`.
6. [ ] Run cljs compile and fix any `^js` / extern warnings in consumer apps.

---

## Related changes elsewhere (not in this file, but same feature)

- **REPL**: `reval-repl-file-layout` nested `flex-layout` + shared `data-a`; `open-file` uses `add-node` with `:dock :center`; outer tab state `:kind :repl-file` with `:code-id` / `:nb-id`.
- Document those in the REPL / `reval-ui` PR description when syncing with `ui-layout`; they are not required in `layout.flexlayout.core` itself.

---

*Last updated to match `reval-ui/src/layout/flexlayout/core.cljs` as vendored in the `reval` repo (documentation-only changes may lag; diff against that file for truth).*
