import { Link } from "react-router-dom";
import "./Navbar.css";

export const Navbar = () => {
    return (
        <nav className="navbar">
            <div className="nav-links">
                <Link to="/">Login</Link>
                <Link to="/register">Register</Link>
                <Link to="/user">User Page</Link>
                <Link to="/admin">Admin Page</Link>
                <Link to="/todo">Todos</Link>
            </div>
        </nav>
    );
};
