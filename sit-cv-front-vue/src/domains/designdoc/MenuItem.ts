interface MenuItem {
  name?: string;
  endpoint?: string;
  children?: MenuItem[];
}

export default MenuItem;