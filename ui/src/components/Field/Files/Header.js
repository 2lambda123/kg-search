import React from "react";
import PropTypes from "prop-types";


const Header = ({node}) => {
  const iconStyle = {marginRight: "5px"};
  return(
    <div className="kgs-hierarchical-files__node_wrapper">
      <div className="kgs-hierarchical-files__node">
        {node.thumbnail ?
          <img height="14" width="12" src={node.thumbnail} alt={node.url} style={iconStyle} />:
          node.type === "file" ? <i className={"fa fa-file-o"} style={iconStyle}/>:null
        }
        <span className="kgs-hierarchical-files__node_name">{node.name}</span>
      </div>
    </div>);
};

Header.propTypes = {
  style: PropTypes.object,
  node: PropTypes.object.isRequired
};

export default Header;