export default interface MenuItem {
  id: string;
  name: string;
  children: MenuItem[];
  endpoint?: string;
}

export class MenuItemUtils {
  public static collectIdsHavingSingleChild(menuItem: MenuItem): string[] {
    let ids = [menuItem.id];

    if (menuItem.children!.length == 1) {
      ids = ids.concat(this.collectIdsHavingSingleChild(menuItem.children![0]));
    }

    return ids;
  }

  public static findByIdRecursively(id: string, menuItem: MenuItem): MenuItem | undefined {
    if (id === menuItem.id) {
      return menuItem;
    }

    if (menuItem.children) {
      // return menuItem.children.find((child) => this.findByIdRecursively(id, child));

      let found: MenuItem | undefined;
      menuItem.children.forEach(child=> {
        if (!found) {
          found = this.findByIdRecursively(id, child);
        }
      });
      return found;
    }
  }
}
