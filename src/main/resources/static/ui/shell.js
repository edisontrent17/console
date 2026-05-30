export function initShell() {
    const tabs = [...document.querySelectorAll("[data-tab-target]")];
    const sections = [...document.querySelectorAll(".workbench-section")];
    const navItems = [...document.querySelectorAll(".rail-nav a")];

    tabs.forEach((tab) => {
        tab.addEventListener("click", () => activate(tab.dataset.tabTarget, true));
    });

    navItems.forEach((item) => {
        item.addEventListener("click", (event) => {
            const target = item.getAttribute("href")?.replace("#", "");
            if (!target) return;
            event.preventDefault();
            activate(target, true);
        });
    });

    window.addEventListener("hashchange", () => activate(currentTarget(), false));
    activate(currentTarget(), false);

    function currentTarget() {
        const target = window.location.hash.replace("#", "");
        return sections.some((section) => section.id === target) ? target : "plan-review";
    }

    function activate(target, updateHash) {
        tabs.forEach((tab) => tab.classList.toggle("active", tab.dataset.tabTarget === target));
        sections.forEach((section) => section.classList.toggle("active", section.id === target));
        navItems.forEach((item) => item.classList.toggle("active", item.getAttribute("href") === `#${target}`));
        if (updateHash && window.location.hash !== `#${target}`) {
            history.pushState(null, "", `#${target}`);
        }
    }
}
